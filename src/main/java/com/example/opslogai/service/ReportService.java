package com.example.opslogai.service;

import com.example.opslogai.entity.Incident;
import com.example.opslogai.entity.IncidentComment;
import com.example.opslogai.entity.ReportDraft;
import com.example.opslogai.repository.ReportDraftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * 障害報告文生成サービス。
 * 現在はテンプレートベース。AI API連携時はgenerateReportText()を差し替える。
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm");

    private final ReportDraftRepository reportDraftRepository;

    @Transactional
    public ReportDraft generateAndSave(Incident incident) {
        String reportText = generateReportText(incident);
        ReportDraft draft = new ReportDraft();
        draft.setIncident(incident);
        draft.setReportText(reportText);
        return reportDraftRepository.save(draft);
    }

    @Transactional(readOnly = true)
    public Optional<ReportDraft> findLatest(Long incidentId) {
        return reportDraftRepository.findFirstByIncidentIdOrderByCreatedAtDesc(incidentId);
    }

    private String generateReportText(Incident incident) {
        StringBuilder sb = new StringBuilder();
        String line = "━".repeat(50);
        sb.append(line).append("\n");
        sb.append("　　　　障　害　報　告　書\n");
        sb.append(line).append("\n\n");

        sb.append("【件名】\n");
        sb.append("  ").append(incident.getTitle()).append("\n\n");

        sb.append("【重要度】").append(incident.getSeverity().getDisplayName()).append("\n");
        sb.append("【ステータス】").append(incident.getStatus().getDisplayName()).append("\n");
        sb.append("【発生日時】").append(incident.getCreatedAt().format(FMT)).append("\n");
        sb.append("【最終更新】").append(incident.getUpdatedAt().format(FMT)).append("\n\n");

        sb.append("【障害概要】\n");
        if (incident.getAiSummary() != null && !incident.getAiSummary().isBlank()) {
            sb.append(indent(incident.getAiSummary())).append("\n\n");
        } else {
            sb.append("  （記載なし）\n\n");
        }

        sb.append("【原因分析】\n");
        if (incident.getAiCause() != null && !incident.getAiCause().isBlank()) {
            sb.append(indent(incident.getAiCause())).append("\n\n");
        } else {
            sb.append("  （記載なし）\n\n");
        }

        sb.append("【対応経緯】\n");
        List<IncidentComment> comments = incident.getComments();
        if (comments != null && !comments.isEmpty()) {
            for (IncidentComment c : comments) {
                sb.append("  [").append(c.getCreatedAt().format(FMT)).append("] ");
                sb.append(c.getUser() != null ? c.getUser().getUsername() : "system").append("\n");
                sb.append("    ").append(c.getComment()).append("\n");
            }
        } else {
            sb.append("  （対応メモなし）\n");
        }
        sb.append("\n");

        sb.append("【今後の対策・再発防止策】\n");
        if (incident.getAiAction() != null && !incident.getAiAction().isBlank()) {
            sb.append(indent(incident.getAiAction())).append("\n\n");
        } else {
            sb.append("  （記載なし）\n\n");
        }

        if (incident.getLogEntry() != null && incident.getLogEntry().getExtractedLog() != null) {
            sb.append("【関連ログ（抜粋）】\n");
            String extracted = incident.getLogEntry().getExtractedLog();
            String[] lines = extracted.split("\n");
            int limit = Math.min(lines.length, 10);
            for (int i = 0; i < limit; i++) {
                sb.append("  ").append(lines[i]).append("\n");
            }
            if (lines.length > 10) {
                sb.append("  ... 他 ").append(lines.length - 10).append(" 行\n");
            }
            sb.append("\n");
        }

        sb.append(line).append("\n");
        sb.append("生成日時: ").append(java.time.LocalDateTime.now().format(FMT)).append("\n");
        sb.append("生成方式: OpsLog AI テンプレート生成\n");
        sb.append("※ 本報告書はシステムが自動生成したドラフトです。内容を確認の上、正式報告へ転記してください。\n");
        sb.append(line).append("\n");

        return sb.toString();
    }

    private String indent(String text) {
        return "  " + text.replace("\n", "\n  ");
    }
}
