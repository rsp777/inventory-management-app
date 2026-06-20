package com.pawar.inventory.app.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pawar.inventory.app.config.AppConstants;
import com.pawar.inventory.app.dto.TransactionLogDTO;
import com.pawar.inventory.app.dto.TransactionLogRequestDTO;
import com.pawar.inventory.app.exception.ResourceNotFoundException;
import com.pawar.inventory.app.model.TransactionLog;
import com.pawar.inventory.app.repository.TransactionLogRepository;

@Service
@Transactional
public class TransactionLogServiceImpl implements TransactionLogService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionLogServiceImpl.class);

    private final TransactionLogRepository transactionLogRepository;

    public TransactionLogServiceImpl(TransactionLogRepository transactionLogRepository) {
		this.transactionLogRepository = transactionLogRepository;
	}

    @Override
    @Transactional(readOnly = true)
    public List<TransactionLog> getAllTransactionLogs() {
        logger.info("Fetching all transaction logs");
        List<TransactionLog> transactionLogs = transactionLogRepository.findAllByOrderByCreatedDttmDesc();
        logger.debug("Fetched {} transaction logs", transactionLogs.size());
        return transactionLogs;
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionLog getTransactionLogById(Long id) {
        logger.info("Fetching transaction log by ID: {}", id);
        TransactionLog transactionLog = transactionLogRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Transaction log not found with ID: " + id));
        logger.debug("Fetched transaction log: {}", transactionLog);
        return transactionLog;
    }

    @Override
	public TransactionLog createTransactionLog(TransactionLogRequestDTO requestDTO) {
		TransactionLog transactionLog = convertToEntity(requestDTO);
        transactionLog.setId(null);
        logger.info("Creating new transaction log: {}", transactionLog);
        TransactionLog createdTransactionLog = transactionLogRepository.save(transactionLog);
        logger.info("Created transaction log with ID: {}", createdTransactionLog.getId());
        return createdTransactionLog;
    }

    @Override
    public void recordMenuTransaction(String transactionName, String data, String source) {
        try {
            TransactionLog transactionLog = new TransactionLog();
            transactionLog.setTransactionName(truncate(transactionName, 255));
            transactionLog.setData(truncate(data, 4000));
            String resolvedSource = resolveSource(source, null);
            transactionLog.setCreatedSource(resolvedSource);
            transactionLog.setLastUpdatedSource(resolvedSource);
            transactionLogRepository.save(transactionLog);
            logger.debug("Recorded menu transaction: {}", transactionName);
        } catch (Exception exception) {
            logger.warn("Unable to persist transaction log for transaction: {}", transactionName, exception);
        }
    }

    @Override
	public TransactionLog updateTransactionLog(Long id, TransactionLogRequestDTO requestDTO) {
        logger.info("Updating transaction log with ID: {}", id);
        TransactionLog existingLog = getTransactionLogById(id);
		existingLog.setTransactionName(requestDTO.getTransactionName());
		existingLog.setData(requestDTO.getData());
		existingLog.setLastUpdatedSource(resolveSource(requestDTO.getSource(), existingLog.getLastUpdatedSource()));
		TransactionLog updatedTransactionLog = transactionLogRepository.save(existingLog);
        logger.info("Updated transaction log with ID: {}", id);
        return updatedTransactionLog;
    }

    @Override
    public void deleteTransactionLog(Long id) {
        logger.info("Deleting transaction log with ID: {}", id);
        if (!transactionLogRepository.existsById(id)) {
			throw new ResourceNotFoundException("Transaction log not found with ID: " + id);
		}
        transactionLogRepository.deleteById(id);
        logger.info("Deleted transaction log with ID: {}", id);
    }

    @Override
    public TransactionLogDTO convertToDTO(TransactionLog transactionLog) {
        TransactionLogDTO dto = new TransactionLogDTO();
        dto.setId(transactionLog.getId());
        dto.setCreatedDttm(transactionLog.getCreatedDttm());
        dto.setLastUpdatedDttm(transactionLog.getLastUpdatedDttm());
        dto.setCreatedSource(transactionLog.getCreatedSource());
        dto.setLastUpdatedSource(transactionLog.getLastUpdatedSource());
        dto.setData(transactionLog.getData());
        dto.setTransactionName(transactionLog.getTransactionName());
        return dto;
    }

    @Override
    public TransactionLog convertToEntity(TransactionLogRequestDTO requestDTO) {
        TransactionLog transactionLog = new TransactionLog();
        transactionLog.setTransactionName(requestDTO.getTransactionName());
        transactionLog.setData(requestDTO.getData());
        String source = resolveSource(requestDTO.getSource(), null);
        transactionLog.setCreatedSource(source);
        transactionLog.setLastUpdatedSource(source);
        return transactionLog;
    }

    private String resolveSource(String requestSource, String fallbackSource) {
        if (requestSource != null && !requestSource.isBlank()) {
            return requestSource;
        }
        if (fallbackSource != null && !fallbackSource.isBlank()) {
            return fallbackSource;
        }
        return AppConstants.Application.AUDIT_SOURCE_SYSTEM;
    }

    private String truncate(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        return value.length() > maxLength ? value.substring(0, maxLength) : value;
    }
}
