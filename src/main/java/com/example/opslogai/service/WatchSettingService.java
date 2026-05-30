package com.example.opslogai.service;

import com.example.opslogai.config.WatchProperties;
import com.example.opslogai.dto.WatchSettingForm;
import com.example.opslogai.entity.WatchSetting;
import com.example.opslogai.repository.WatchSettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WatchSettingService {

    private final WatchSettingRepository watchSettingRepository;
    private final WatchProperties watchProperties;

    /**
     * DBの設定を取得する。レコードが存在しない場合は application.properties の値で初期化して保存。
     * アプリ起動時（SmartLifecycle / DataInitializer）から安全に呼び出し可能。
     */
    @Transactional
    public WatchSetting getOrInit() {
        return watchSettingRepository.findTopByOrderByIdAsc()
                .orElseGet(this::initFromProperties);
    }

    @Transactional
    public WatchSetting save(WatchSettingForm form) {
        WatchSetting setting = watchSettingRepository.findTopByOrderByIdAsc()
                .orElse(new WatchSetting());
        setting.setEnabled(form.isEnabled());
        setting.setDirectoryPath(form.getDirectoryPath().trim());
        setting.setFileExtensions(normalizeExtensions(form.getFileExtensions()));
        WatchSetting saved = watchSettingRepository.save(setting);
        log.info("監視設定を保存しました: enabled={}, dir={}, extensions={}",
                saved.isEnabled(), saved.getDirectoryPath(), saved.getFileExtensions());
        return saved;
    }

    private WatchSetting initFromProperties() {
        String dir = watchProperties.getDirectory();
        if (dir == null || dir.isBlank()) dir = "C:/opslog-ai/watch";

        WatchSetting setting = new WatchSetting();
        setting.setEnabled(watchProperties.isEnabled());
        setting.setDirectoryPath(dir);
        setting.setFileExtensions(String.join(",", watchProperties.getFileExtensions()));
        log.info("watch_settings: 初期レコードを application.properties から生成します (enabled={}, dir={})",
                setting.isEnabled(), setting.getDirectoryPath());
        return watchSettingRepository.save(setting);
    }

    /**
     * 拡張子文字列を正規化：先頭の "." 補完、スペース除去、小文字化。
     * 例: "log, TXT" → ".log,.txt"
     */
    private String normalizeExtensions(String raw) {
        StringBuilder sb = new StringBuilder();
        for (String ext : raw.split(",")) {
            String e = ext.trim().toLowerCase();
            if (e.isEmpty()) continue;
            if (!e.startsWith(".")) e = "." + e;
            if (sb.length() > 0) sb.append(",");
            sb.append(e);
        }
        return sb.isEmpty() ? raw.trim() : sb.toString();
    }
}
