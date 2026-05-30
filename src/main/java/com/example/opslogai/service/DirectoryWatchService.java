package com.example.opslogai.service;

import com.example.opslogai.entity.WatchSetting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 指定ディレクトリを WatchService で監視し、対象拡張子ファイルの作成を検知する。
 *
 * 起動設定は watch_settings テーブル (WatchSettingService) から読み込む。
 * applySettings() で再起動なしの動的反映が可能。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DirectoryWatchService implements SmartLifecycle {

    private final WatchSettingService watchSettingService;
    private final LogFileImportService logFileImportService;

    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread watchThread;
    private WatchService watchService;

    // ---- SmartLifecycle ----

    @Override
    public void start() {
        WatchSetting setting = watchSettingService.getOrInit();
        startInternal(setting);
    }

    @Override
    public void stop() {
        stopInternal();
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    // ---- 画面からの動的反映 (WatchSettingController から呼び出し) ----

    /**
     * 設定を即時反映する。現在稼働中の監視を停止してから新設定で再起動。
     * Web リクエストスレッドから呼び出される。
     */
    public synchronized void applySettings(WatchSetting setting) {
        log.info("監視設定を動的に適用します: enabled={}, dir={}",
                setting.isEnabled(), setting.getDirectoryPath());
        stopInternal();
        if (setting.isEnabled()) {
            startInternal(setting);
        }
    }

    // ---- 内部実装 ----

    private synchronized void startInternal(WatchSetting setting) {
        if ("true".equalsIgnoreCase(System.getenv("OPSLOG_WATCH_DISABLED"))) {
            log.info("ディレクトリ監視: OPSLOG_WATCH_DISABLED=true のため強制無効化されています（公開環境）");
            return;
        }
        if (!setting.isEnabled()) {
            log.info("ディレクトリ監視: 無効");
            return;
        }

        String directoryPath = setting.getDirectoryPath();
        if (directoryPath == null || directoryPath.isBlank()) {
            log.warn("ディレクトリ監視: directoryPath が未設定のため起動をスキップします");
            return;
        }

        Path dir = Path.of(directoryPath);
        if (!Files.isDirectory(dir)) {
            log.warn("ディレクトリ監視: ディレクトリが存在しません: {} — 監視を起動しません", dir);
            return;
        }

        try {
            watchService = FileSystems.getDefault().newWatchService();
            dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
            running.set(true);

            List<String> extensions = setting.getFileExtensionList();
            watchThread = new Thread(() -> watchLoop(extensions), "opslog-watch-thread");
            watchThread.setDaemon(true);
            watchThread.start();

            log.info("ディレクトリ監視を開始しました: {} (対象拡張子: {})", dir.toAbsolutePath(), extensions);
        } catch (IOException e) {
            log.error("ディレクトリ監視の起動に失敗しました", e);
        }
    }

    private synchronized void stopInternal() {
        running.set(false);
        if (watchService != null) {
            try {
                watchService.close();
            } catch (IOException e) {
                log.warn("WatchService のクローズ中にエラーが発生しました", e);
            }
            watchService = null;
        }
        if (watchThread != null) {
            watchThread.interrupt();
            try {
                watchThread.join(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            watchThread = null;
        }
        log.info("ディレクトリ監視を停止しました");
    }

    private void watchLoop(List<String> extensions) {
        log.info("監視ループ開始 (対象拡張子: {})", extensions);
        while (running.get()) {
            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (ClosedWatchServiceException e) {
                break;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    log.warn("WatchService OVERFLOW: イベントが失われた可能性があります");
                    continue;
                }

                @SuppressWarnings("unchecked")
                WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
                Path fileName = pathEvent.context();
                String lowerName = fileName.toString().toLowerCase();

                log.info("ファイルイベント検知 [{}]: {}", kind.name(), fileName);

                boolean matched = extensions.stream().anyMatch(lowerName::endsWith);
                if (!matched) {
                    log.info("スキップ (対象外拡張子): {} — 監視対象: {}", fileName, extensions);
                    continue;
                }

                Path dir = (Path) key.watchable();
                Path fullPath = dir.resolve(fileName);

                log.info("対象ファイル確認: {} — 書き込み完了待機中...", fullPath);
                waitForFileReady(fullPath);
                log.info("ファイル取込を開始します: {}", fullPath);

                try {
                    logFileImportService.importFile(fullPath);
                } catch (Exception e) {
                    log.error("取込失敗: {} — {}", fullPath, e.getMessage(), e);
                }
            }

            if (!key.reset()) {
                log.warn("監視ディレクトリへのアクセスが失われました。監視を停止します。");
                break;
            }
        }

        running.set(false);
        log.info("監視ループを終了しました");
    }

    /**
     * ファイルサイズが連続して安定するまで待機する（コピー中ファイルの誤読み防止）。
     */
    private void waitForFileReady(Path path) {
        long previousSize = -1;
        int stableCount = 0;
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(500);
                long currentSize = Files.exists(path) ? Files.size(path) : -1;
                if (currentSize == previousSize) {
                    stableCount++;
                    if (stableCount >= 2) return;
                } else {
                    stableCount = 0;
                }
                previousSize = currentSize;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } catch (IOException e) {
                return;
            }
        }
    }
}
