package com.example.opslogai.service;

import com.example.opslogai.dto.ChatResponse;
import com.example.opslogai.dto.ChatSearchResult;
import com.example.opslogai.entity.*;
import com.example.opslogai.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatSearchService {

    private static final int MAX_RESULTS = 10;

    private final IncidentRepository incidentRepository;
    private final LogEntryRepository logEntryRepository;
    private final IncidentCommentRepository incidentCommentRepository;
    private final ReportDraftRepository reportDraftRepository;

    public ChatResponse search(String query) {
        if (query == null || query.isBlank()) {
            return ChatResponse.builder()
                    .message("検索キーワードを入力してください。")
                    .results(List.of())
                    .hasResults(false)
                    .query("")
                    .build();
        }

        String trimmed = query.trim();
        IncidentStatus statusFilter = detectStatus(trimmed);
        Severity severityFilter = detectSeverity(trimmed);

        List<ChatSearchResult> results;
        if (statusFilter != null) {
            results = searchByStatus(statusFilter);
        } else if (severityFilter != null) {
            results = searchBySeverity(severityFilter);
        } else {
            results = searchByKeywords(trimmed);
        }

        String message = generateMessage(trimmed, results, statusFilter, severityFilter);

        return ChatResponse.builder()
                .message(message)
                .results(results)
                .hasResults(!results.isEmpty())
                .query(trimmed)
                .build();
    }

    // ----------------------------------------------------------------
    // Search strategies
    // ----------------------------------------------------------------

    private List<ChatSearchResult> searchByStatus(IncidentStatus status) {
        return incidentRepository.findByStatusOrderByCreatedAtDesc(status).stream()
                .limit(MAX_RESULTS)
                .map(i -> toIncidentResult(i, "ステータス"))
                .toList();
    }

    private List<ChatSearchResult> searchBySeverity(Severity severity) {
        return incidentRepository.findBySeverityOrderByCreatedAtDesc(severity).stream()
                .limit(MAX_RESULTS)
                .map(i -> toIncidentResult(i, "重要度"))
                .toList();
    }

    private List<ChatSearchResult> searchByKeywords(String query) {
        String[] keywords = query.split("\\s+");
        List<ChatSearchResult> results = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (String keyword : keywords) {
            if (keyword.isBlank() || keyword.length() < 2) continue;
            String pattern = "%" + keyword + "%";

            for (Incident i : incidentRepository.searchByPattern(pattern)) {
                if (seen.add("incident:" + i.getId())) {
                    results.add(toIncidentResult(i, "タイトル/AI解析"));
                }
            }
            for (LogEntry l : logEntryRepository.searchByPattern(pattern)) {
                if (seen.add("log:" + l.getId())) {
                    results.add(toLogResult(l));
                }
            }
            for (IncidentComment c : incidentCommentRepository.searchByPattern(pattern)) {
                if (seen.add("incident:" + c.getIncident().getId())) {
                    results.add(toIncidentResult(c.getIncident(), "コメント"));
                }
            }
            for (ReportDraft r : reportDraftRepository.searchByPattern(pattern)) {
                if (seen.add("incident:" + r.getIncident().getId())) {
                    results.add(toIncidentResult(r.getIncident(), "報告書"));
                }
            }

            if (results.size() >= MAX_RESULTS) break;
        }

        return results.size() > MAX_RESULTS ? results.subList(0, MAX_RESULTS) : results;
    }

    // ----------------------------------------------------------------
    // Response message generation
    // Replace this method body with an AI API call for natural language responses.
    // Input: query, DB search results (results), optional filters
    // Output: natural language answer string
    // ----------------------------------------------------------------
    private String generateMessage(String query, List<ChatSearchResult> results,
                                   IncidentStatus statusFilter, Severity severityFilter) {
        if (results.isEmpty()) {
            return "該当するログ・障害チケットは見つかりませんでした。";
        }

        long incidentCount = results.stream().filter(r -> "incident".equals(r.getType())).count();
        long logCount = results.stream().filter(r -> "log".equals(r.getType())).count();

        StringBuilder sb = new StringBuilder();
        if (statusFilter != null) {
            sb.append("「").append(statusFilter.getDisplayName()).append("」の障害が")
              .append(incidentCount).append("件見つかりました。");
        } else if (severityFilter != null) {
            sb.append("重要度「").append(severityFilter.getDisplayName()).append("」の障害が")
              .append(incidentCount).append("件見つかりました。");
        } else {
            sb.append("「").append(query).append("」に関連する情報が")
              .append(results.size()).append("件見つかりました。");
            if (incidentCount > 0) sb.append("\n障害チケット：").append(incidentCount).append("件");
            if (logCount > 0) sb.append("\nログエントリ：").append(logCount).append("件");
        }
        return sb.toString();
    }

    // ----------------------------------------------------------------
    // Keyword / filter detection
    // ----------------------------------------------------------------

    private IncidentStatus detectStatus(String query) {
        if (query.contains("未対応")) return IncidentStatus.OPEN;
        if (query.contains("調査中")) return IncidentStatus.INVESTIGATING;
        if (query.contains("対応済")) return IncidentStatus.RESOLVED;
        if (query.contains("保留")) return IncidentStatus.ON_HOLD;
        return null;
    }

    private Severity detectSeverity(String query) {
        if (query.contains("緊急")) return Severity.CRITICAL;
        if (query.contains("重要度が高") || query.contains("重要度高") || query.contains("高リスク")) return Severity.HIGH;
        if (query.contains("重要度が低") || query.contains("重要度低") || query.contains("低リスク")) return Severity.LOW;
        if (query.contains("重要度が中") || query.contains("重要度中") || query.contains("中リスク")) return Severity.MEDIUM;
        return null;
    }

    // ----------------------------------------------------------------
    // DTO mapping
    // ----------------------------------------------------------------

    private ChatSearchResult toIncidentResult(Incident incident, String matchedField) {
        return ChatSearchResult.builder()
                .type("incident")
                .linkUrl("/incidents/" + incident.getId())
                .title(incident.getTitle())
                .statusDisplay(incident.getStatus() != null ? incident.getStatus().getDisplayName() : "")
                .severityDisplay(incident.getSeverity() != null ? incident.getSeverity().getDisplayName() : "")
                .statusCss(statusCss(incident.getStatus()))
                .severityCss(severityCss(incident.getSeverity()))
                .summary(snippet(incident.getAiSummary()))
                .matchedField(matchedField)
                .build();
    }

    private ChatSearchResult toLogResult(LogEntry log) {
        return ChatSearchResult.builder()
                .type("log")
                .linkUrl("/log/result/" + log.getId())
                .title(log.getTitle())
                .summary(snippet(log.getExtractedLog()))
                .matchedField("ログ内容")
                .build();
    }

    private String snippet(String text) {
        if (text == null || text.isBlank()) return "";
        String first = text.split("\n")[0].trim();
        return first.length() > 90 ? first.substring(0, 90) + "…" : first;
    }

    private String statusCss(IncidentStatus status) {
        if (status == null) return "";
        return switch (status) {
            case OPEN -> "badge-open";
            case INVESTIGATING -> "badge-investigating";
            case RESOLVED -> "badge-resolved";
            case ON_HOLD -> "badge-on-hold";
        };
    }

    private String severityCss(Severity severity) {
        if (severity == null) return "";
        return switch (severity) {
            case CRITICAL -> "severity-critical";
            case HIGH -> "severity-high";
            case MEDIUM -> "severity-medium";
            case LOW -> "severity-low";
        };
    }
}
