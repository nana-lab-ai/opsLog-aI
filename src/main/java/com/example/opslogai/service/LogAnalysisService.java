package com.example.opslogai.service;

import com.example.opslogai.dto.LogAnalysisRequest;
import com.example.opslogai.dto.LogAnalysisResult;
import com.example.opslogai.entity.LogEntry;
import com.example.opslogai.entity.User;
import com.example.opslogai.repository.LogEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LogAnalysisService {

    private static final List<String> KEYWORDS = List.of(
            "ERROR", "WARN", "Exception", "ORA-", "failed", "timeout", "refused", "denied"
    );

    private final LogEntryRepository logEntryRepository;
    private final AiAnalysisService aiAnalysisService;

    @Transactional
    public LogAnalysisResult analyzeAndSave(LogAnalysisRequest request, User user) {
        List<String> extracted = extractErrorLines(request.getRawLog());
        String extractedText = String.join("\n", extracted);

        LogEntry entry = new LogEntry();
        entry.setUser(user);
        entry.setTitle(request.getTitle());
        entry.setRawLog(request.getRawLog());
        entry.setExtractedLog(extractedText);
        LogEntry saved = logEntryRepository.save(entry);

        LogAnalysisResult result = new LogAnalysisResult();
        result.setLogEntryId(saved.getId());
        result.setTitle(saved.getTitle());
        result.setExtractedLines(extracted);
        result.setTotalLines(request.getRawLog().split("\n").length);
        result.setExtractedCount(extracted.size());
        result.setAiSummary(aiAnalysisService.generateSummary(extracted));
        result.setAiCause(aiAnalysisService.generateCause(extracted));
        result.setAiAction(aiAnalysisService.generateAction(extracted));
        return result;
    }

    public List<String> extractErrorLines(String rawLog) {
        if (rawLog == null || rawLog.isBlank()) return List.of();
        return Arrays.stream(rawLog.split("\n"))
                .filter(line -> KEYWORDS.stream()
                        .anyMatch(kw -> line.toLowerCase().contains(kw.toLowerCase())))
                .collect(Collectors.toList());
    }
}
