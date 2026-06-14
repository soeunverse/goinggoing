package com.goinggoing.goinggoing.domain.sync.repository;

import com.goinggoing.goinggoing.domain.sync.entity.ExternalSyncLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExternalSyncLogRepository extends JpaRepository<ExternalSyncLog, Long> {

	List<ExternalSyncLog> findAllByOrderByRequestedAtDesc();
}
