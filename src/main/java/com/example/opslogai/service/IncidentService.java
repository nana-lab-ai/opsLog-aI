package com.example.opslogai.service;

import com.example.opslogai.dto.IncidentForm;
import com.example.opslogai.entity.*;
import com.example.opslogai.exception.DemoDataLimitException;
import com.example.opslogai.repository.IncidentCommentRepository;
import com.example.opslogai.repository.IncidentRepository;
import com.example.opslogai.repository.LogEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class IncidentService {

    static final long MAX_INCIDENTS = 50L;

    private final IncidentRepository incidentRepository;
    private final IncidentCommentRepository commentRepository;
    private final LogEntryRepository logEntryRepository;

    @Transactional
    public Incident createIncident(IncidentForm form, User user) {
        if (incidentRepository.count() >= MAX_INCIDENTS) {
            throw new DemoDataLimitException(
                    "デモ環境のため障害チケットの上限（" + MAX_INCIDENTS + "件）に達しました。" +
                    "登録データはリセットされる場合があります。");
        }
        Incident incident = new Incident();
        incident.setTitle(form.getTitle());
        incident.setSeverity(form.getSeverity());
        incident.setStatus(form.getStatus() != null ? form.getStatus() : IncidentStatus.OPEN);
        incident.setUser(user);
        incident.setAiSummary(form.getAiSummary());
        incident.setAiCause(form.getAiCause());
        incident.setAiAction(form.getAiAction());

        if (form.getLogEntryId() != null) {
            logEntryRepository.findById(form.getLogEntryId())
                    .ifPresent(incident::setLogEntry);
        }
        return incidentRepository.save(incident);
    }

    @Transactional
    public void updateStatus(Long id, IncidentStatus newStatus) {
        Incident incident = findById(id);
        incident.setStatus(newStatus);
        incidentRepository.save(incident);
    }

    @Transactional
    public void addComment(Long incidentId, String commentText, User user) {
        Incident incident = findById(incidentId);
        IncidentComment comment = new IncidentComment();
        comment.setIncident(incident);
        comment.setUser(user);
        comment.setComment(commentText);
        commentRepository.save(comment);
        incident.setUpdatedAt(java.time.LocalDateTime.now());
        incidentRepository.save(incident);
    }

    @Transactional(readOnly = true)
    public Incident findById(Long id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("障害チケットが見つかりません: " + id));
    }

    @Transactional(readOnly = true)
    public List<Incident> findAll() {
        return incidentRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<Incident> findByStatus(IncidentStatus status) {
        return incidentRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    public long countByStatus(IncidentStatus status) {
        return incidentRepository.countByStatus(status);
    }
}
