package com.example.opslogai.repository;

import com.example.opslogai.entity.WatchSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WatchSettingRepository extends JpaRepository<WatchSetting, Long> {
    Optional<WatchSetting> findTopByOrderByIdAsc();
}
