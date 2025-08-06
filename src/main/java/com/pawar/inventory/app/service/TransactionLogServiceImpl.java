package com.pawar.inventory.app.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.pawar.inventory.app.model.TransactionLog;
import com.pawar.inventory.app.repository.TransactionLogRepository;

@Service
public class TransactionLogServiceImpl implements TransactionLogService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionLogServiceImpl.class);

    @Autowired
    private TransactionLogRepository transactionLogRepository;

    @Override
    public List<TransactionLog> getAllTransactionLogs() {
        logger.info("Fetching all transaction logs");
        List<TransactionLog> transactionLogs = transactionLogRepository.findAll();
        logger.debug("Fetched {} transaction logs", transactionLogs.size());
        return transactionLogs;
    }

    @Override
    public TransactionLog getTransactionLogById(Long id) {
        logger.info("Fetching transaction log by ID: {}", id);
        TransactionLog transactionLog = transactionLogRepository.findById(id).orElse(null);
        if (transactionLog == null) {
            logger.warn("Transaction log with ID {} not found", id);
        } else {
            logger.debug("Fetched transaction log: {}", transactionLog);
        }
        return transactionLog;
    }

    @Override
    public TransactionLog createTransactionLog(TransactionLog transactionLog) {
        logger.info("Creating new transaction log: {}", transactionLog);
        TransactionLog createdTransactionLog = transactionLogRepository.save(transactionLog);
        logger.info("Created transaction log with ID: {}", createdTransactionLog.getId());
        return createdTransactionLog;
    }

    @Override
    public TransactionLog updateTransactionLog(Long id, TransactionLog transactionLog) {
        logger.info("Updating transaction log with ID: {}", id);
        transactionLog.setId(id);
        TransactionLog updatedTransactionLog = transactionLogRepository.save(transactionLog);
        logger.info("Updated transaction log with ID: {}", id);
        return updatedTransactionLog;
    }

    @Override
    public void deleteTransactionLog(Long id) {
        logger.info("Deleting transaction log with ID: {}", id);
        transactionLogRepository.deleteById(id);
        logger.info("Deleted transaction log with ID: {}", id);
    }
}
