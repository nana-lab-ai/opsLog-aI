package com.example.opslogai.repository;

import com.example.opslogai.entity.IncidentComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IncidentCommentRepository extends JpaRepository<IncidentComment, Long> {
    List<IncidentComment> findByIncidentIdOrderByCreatedAtAsc(Long incidentId);

    @Query("SELECT c FROM IncidentComment c JOIN FETCH c.incident WHERE c.comment LIKE :pattern ORDER BY c.createdAt DESC")
    List<IncidentComment> searchByPattern(@Param("pattern") String pattern);
}
