package com.pawar.inventory.app.service;

import java.util.List;

import com.pawar.inventory.app.dto.TransactionLogDTO;
import com.pawar.inventory.app.dto.TransactionLogRequestDTO;
import com.pawar.inventory.app.model.TransactionLog;

public interface TransactionLogService {
    List<TransactionLog> getAllTransactionLogs();
    TransactionLog getTransactionLogById(Long id);
    TransactionLog createTransactionLog(TransactionLogRequestDTO requestDTO);
    TransactionLog updateTransactionLog(Long id, TransactionLogRequestDTO requestDTO);
    void deleteTransactionLog(Long id);
    TransactionLogDTO convertToDTO(TransactionLog transactionLog);
    TransactionLog convertToEntity(TransactionLogRequestDTO requestDTO);
}
