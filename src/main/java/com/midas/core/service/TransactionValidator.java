package com.midas.core.service;

import org.springframework.stereotype.Service;
import com.midas.core.dto.TransactionDto;

@Service
public class TransactionValidator {

    public boolean validate(TransactionDto dto) {
        if (dto == null) return false;
        if (dto.getTxnId() == null || dto.getTxnId().isEmpty()) return false;
        if (dto.getAmount() <= 0) return false;
        if (dto.getSenderId() == null || dto.getRecipientId() == null) return false;
        if (dto.getSenderId().equals(dto.getRecipientId())) return false;
        return true;
    }
}
