package com.example.opslogai.repository;

import com.example.opslogai.entity.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {
    List<LogEntry> findAllByOrderByCreatedAtDesc();

    @Query("SELECT l FROM LogEntry l WHERE l.title LIKE :pattern OR l.rawLog LIKE :pattern OR l.extractedLog LIKE :pattern ORDER BY l.createdAt DESC")
    List<LogEntry> searchByPattern(@Param("pattern") String pattern);
}
