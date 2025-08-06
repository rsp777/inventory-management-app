package com.pawar.inventory.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pawar.inventory.app.model.TransactionLog;

public interface TransactionLogRepository extends JpaRepository<TransactionLog, Long> {
    // Additional query methods can be defined here if needed
}
