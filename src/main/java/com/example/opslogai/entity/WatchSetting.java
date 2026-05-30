package com.example.opslogai.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "watch_settings")
@Getter
@Setter
@NoArgsConstructor
public class WatchSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean enabled;

    @Column(nullable = false)
    private String directoryPath;

    /** カンマ区切りで保持: ".log,.txt" */
    @Column(nullable = false)
    private String fileExtensions;

    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /** 拡張子をリストに変換して返す（小文字正規化済み） */
    public List<String> getFileExtensionList() {
        return Arrays.stream(fileExtensions.split(","))
                .map(s -> s.trim().toLowerCase())
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
