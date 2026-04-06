package com.pawar.inventory.app.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pawar.inventory.app.dto.TransactionLogDTO;
import com.pawar.inventory.app.dto.TransactionLogRequestDTO;
import com.pawar.inventory.app.service.TransactionLogService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transactionLogs")
public class TransactionLogController {

    private final TransactionLogService transactionLogService;

    public TransactionLogController(TransactionLogService transactionLogService) {
		this.transactionLogService = transactionLogService;
	}

    @GetMapping
    public ResponseEntity<List<TransactionLogDTO>> getAllTransactionLogs() {
        List<TransactionLogDTO> transactionLogs = transactionLogService.getAllTransactionLogs().stream()
				.map(transactionLogService::convertToDTO)
				.collect(Collectors.toList());
        return ResponseEntity.ok(transactionLogs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionLogDTO> getTransactionLogById(@PathVariable Long id) {
        TransactionLogDTO transactionLog = transactionLogService.convertToDTO(transactionLogService.getTransactionLogById(id));
        return ResponseEntity.ok(transactionLog);
    }

    @PostMapping
    public ResponseEntity<TransactionLogDTO> createTransactionLog(@Valid @RequestBody TransactionLogRequestDTO transactionLog) {
        TransactionLogDTO createdTransactionLog = transactionLogService
				.convertToDTO(transactionLogService.createTransactionLog(transactionLog));
        return ResponseEntity.status(201).body(createdTransactionLog);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionLogDTO> updateTransactionLog(@PathVariable Long id,
			@Valid @RequestBody TransactionLogRequestDTO transactionLog) {
        TransactionLogDTO updatedTransactionLog = transactionLogService
				.convertToDTO(transactionLogService.updateTransactionLog(id, transactionLog));
        return ResponseEntity.ok(updatedTransactionLog);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransactionLog(@PathVariable Long id) {
        transactionLogService.deleteTransactionLog(id);
        return ResponseEntity.noContent().build();
    }
}
