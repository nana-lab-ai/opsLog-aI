package com.example.opslogai.repository;

import com.example.opslogai.entity.ReportDraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReportDraftRepository extends JpaRepository<ReportDraft, Long> {
    Optional<ReportDraft> findFirstByIncidentIdOrderByCreatedAtDesc(Long incidentId);

    @Query("SELECT r FROM ReportDraft r JOIN FETCH r.incident WHERE r.reportText LIKE :pattern ORDER BY r.createdAt DESC")
    List<ReportDraft> searchByPattern(@Param("pattern") String pattern);
}
