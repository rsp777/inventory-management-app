package com.pawar.inventory.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pawar.inventory.app.model.TransactionLog;

public interface TransactionLogRepository extends JpaRepository<TransactionLog, Long> {
	List<TransactionLog> findAllByOrderByCreatedDttmDesc();
}
