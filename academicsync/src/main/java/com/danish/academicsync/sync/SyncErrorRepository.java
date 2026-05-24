package com.danish.academicsync.sync;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SyncErrorRepository extends JpaRepository<SyncError, Long> {
    List<SyncError> findByResolvedFalseOrderByCreatedAtDesc();

    List<SyncError> findByResolvedFalseAndEntityTypeOrderByCreatedAtDesc(String entityType);

    long countByResolvedFalse();
}
