package com.pawar.inventory.app.service;

import java.util.List;
import com.pawar.inventory.app.model.TransactionLog;

public interface TransactionLogService {
    List<TransactionLog> getAllTransactionLogs();
    TransactionLog getTransactionLogById(Long id);
    TransactionLog createTransactionLog(TransactionLog transactionLog);
    TransactionLog updateTransactionLog(Long id, TransactionLog transactionLog);
    void deleteTransactionLog(Long id);
}
