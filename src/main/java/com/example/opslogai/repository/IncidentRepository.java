package com.example.opslogai.repository;

import com.example.opslogai.entity.Incident;
import com.example.opslogai.entity.IncidentStatus;
import com.example.opslogai.entity.Severity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findAllByOrderByCreatedAtDesc();
    List<Incident> findByStatusOrderByCreatedAtDesc(IncidentStatus status);
    List<Incident> findBySeverityOrderByCreatedAtDesc(Severity severity);
    long countByStatus(IncidentStatus status);

    @Query("SELECT i FROM Incident i WHERE i.title LIKE :pattern OR i.aiSummary LIKE :pattern OR i.aiCause LIKE :pattern OR i.aiAction LIKE :pattern ORDER BY i.createdAt DESC")
    List<Incident> searchByPattern(@Param("pattern") String pattern);
}
