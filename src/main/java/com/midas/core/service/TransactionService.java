package com.midas.core.service;

import com.google.gson.Gson;
import com.midas.core.dto.TransactionDto;
import com.midas.core.model.Transaction;
import com.midas.core.model.TransactionRecord;
import com.midas.core.model.User;
import com.midas.core.repository.TransactionRecordRepository;
import com.midas.core.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionValidator validator;
    private final TransactionRecordRepository transactionRecordRepository;
    private final UserRepository userRepository;
    private final IncentiveClient incentiveClient;
    private final Gson gson = new Gson();

    public TransactionService(TransactionValidator validator,
                              TransactionRecordRepository transactionRecordRepository,
                              UserRepository userRepository,
                              IncentiveClient incentiveClient) {
        this.validator = validator;
        this.transactionRecordRepository = transactionRecordRepository;
        this.userRepository = userRepository;
        this.incentiveClient = incentiveClient;
    }

    @Transactional
    public boolean processIncomingTransaction(TransactionDto dto) {
        if (!validator.validate(dto)) {
            log.warn("Discarding transaction {} due to basic validation failure", dto != null ? dto.getTxnId() : "unknown");
            return false;
        }

        Optional<User> senderOpt = userRepository.findById(dto.getSenderId());
        Optional<User> recipientOpt = userRepository.findById(dto.getRecipientId());
        if (senderOpt.isEmpty() || recipientOpt.isEmpty()) {
            log.warn("Discarding transaction {} due to missing sender or recipient", dto.getTxnId());
            return false;
        }

        User sender = senderOpt.get();
        User recipient = recipientOpt.get();
        if (sender.getBalance() < dto.getAmount()) {
            log.warn("Discarding transaction {} due to insufficient funds for sender {}", dto.getTxnId(), sender.getId());
            return false;
        }

        Transaction incentiveRequest = new Transaction(dto.getSenderId(), dto.getRecipientId(), dto.getAmount());
        double incentiveAmount = incentiveClient.fetchIncentive(incentiveRequest);

        sender.setBalance(sender.getBalance() - dto.getAmount());
        recipient.setBalance(recipient.getBalance() + dto.getAmount() + incentiveAmount);
        userRepository.save(sender);
        userRepository.save(recipient);

        TransactionRecord record = new TransactionRecord();
        record.setTxnId(dto.getTxnId());
        record.setAmount(dto.getAmount());
        record.setIncentive(incentiveAmount);
        record.setTimestamp(System.currentTimeMillis());
        record.setStatus("VALID");
        record.setSender(sender);
        record.setRecipient(recipient);
        transactionRecordRepository.save(record);

        log.info("Processed transaction {} transferring {} (+{} incentive) from {} to {}",
                dto.getTxnId(), dto.getAmount(), incentiveAmount, sender.getId(), recipient.getId());
        return true;
    }

    public void processIncomingTransaction(String json) {
        TransactionDto dto = gson.fromJson(json, TransactionDto.class);
        processIncomingTransaction(dto);
    }

    public Optional<TransactionRecord> findByTxnId(String txnId) {
        return transactionRecordRepository.findByTxnId(txnId);
    }
}

