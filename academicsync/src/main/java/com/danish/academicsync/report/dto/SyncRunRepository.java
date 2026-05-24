package com.danish.academicsync.sync;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SyncRunRepository extends JpaRepository<SyncRun, Long> {
    long countByStatus(String status);
}