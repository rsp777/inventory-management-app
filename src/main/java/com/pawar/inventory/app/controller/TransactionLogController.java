package com.pawar.inventory.app.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.pawar.inventory.app.model.TransactionLog;
import com.pawar.inventory.app.service.TransactionLogService;

@RestController
@RequestMapping("/api/transactionLogs")
public class TransactionLogController {

    @Autowired
    private TransactionLogService transactionLogService;

    @GetMapping
    public ResponseEntity<List<TransactionLog>> getAllTransactionLogs() {
        List<TransactionLog> transactionLogs = transactionLogService.getAllTransactionLogs();
        return ResponseEntity.ok(transactionLogs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionLog> getTransactionLogById(@PathVariable Long id) {
        TransactionLog transactionLog = transactionLogService.getTransactionLogById(id);
        return ResponseEntity.ok(transactionLog);
    }

    @PostMapping
    public ResponseEntity<TransactionLog> createTransactionLog(@RequestBody TransactionLog transactionLog) {
        TransactionLog createdTransactionLog = transactionLogService.createTransactionLog(transactionLog);
        return ResponseEntity.ok(createdTransactionLog);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionLog> updateTransactionLog(@PathVariable Long id, @RequestBody TransactionLog transactionLog) {
        TransactionLog updatedTransactionLog = transactionLogService.updateTransactionLog(id, transactionLog);
        return ResponseEntity.ok(updatedTransactionLog);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransactionLog(@PathVariable Long id) {
        transactionLogService.deleteTransactionLog(id);
        return ResponseEntity.noContent().build();
    }
}
