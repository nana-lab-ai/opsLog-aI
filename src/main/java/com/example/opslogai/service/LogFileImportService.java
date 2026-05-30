package com.example.opslogai.service;

import com.example.opslogai.entity.ImportSource;
import com.example.opslogai.entity.LogEntry;
import com.example.opslogai.repository.LogEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * 監視ディレクトリで検知したログファイルを取り込み、LogEntry として保存するサービス。
 * user は null（自動取込のためログインユーザーが存在しない）。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LogFileImportService {

    private static final Charset FALLBACK_CHARSET = Charset.forName("Windows-31J");

    private final LogEntryRepository logEntryRepository;
    private final LogAnalysisService logAnalysisService;

    @Transactional
    public LogEntry importFile(Path filePath) throws IOException {
        log.info("取込処理開始: {}", filePath);

        // ファイル読み込み（UTF-8 優先、失敗時は Windows-31J でリトライ）
        String rawLog = readWithFallback(filePath);
        if (rawLog == null) return null;

        if (rawLog.isBlank()) {
            log.warn("スキップ: 空ファイル {}", filePath.getFileName());
            return null;
        }

        List<String> extracted = logAnalysisService.extractErrorLines(rawLog);
        String extractedText = String.join("\n", extracted);

        LogEntry entry = new LogEntry();
        entry.setTitle("[自動取込] " + filePath.getFileName().toString());
        entry.setRawLog(rawLog);
        entry.setExtractedLog(extractedText);
        entry.setImportSource(ImportSource.AUTO);
        entry.setSourceFileName(filePath.getFileName().toString());

        try {
            LogEntry saved = logEntryRepository.save(entry);
            log.info("自動取込完了: {} (ID={}, 総行数={}行, 抽出行数={}行)",
                    filePath.getFileName(),
                    saved.getId(),
                    rawLog.split("\n").length,
                    extracted.size());
            return saved;
        } catch (Exception e) {
            log.error("取込失敗 — DB保存エラー: {} — {}", filePath.getFileName(), e.getMessage(), e);
            throw e;
        }
    }

    private String readWithFallback(Path filePath) throws IOException {
        try {
            String content = Files.readString(filePath, StandardCharsets.UTF_8);
            log.debug("ファイル読み込み成功 (UTF-8): {}", filePath.getFileName());
            return content;
        } catch (MalformedInputException e) {
            log.warn("UTF-8 で読み込めません。Windows-31J (Shift-JIS) で再試行します: {}",
                    filePath.getFileName());
        }

        try {
            String content = Files.readString(filePath, FALLBACK_CHARSET);
            log.info("ファイル読み込み成功 (Windows-31J): {}", filePath.getFileName());
            return content;
        } catch (IOException e) {
            log.error("取込失敗 — ファイル読み込みエラー: {} — {}", filePath.getFileName(), e.getMessage(), e);
            throw e;
        }
    }
}
