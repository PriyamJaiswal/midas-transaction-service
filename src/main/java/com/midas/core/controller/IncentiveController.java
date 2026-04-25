package com.midas.core.controller;

import com.midas.core.model.TransactionRecord;
import com.midas.core.repository.TransactionRecordRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/incentives")
public class IncentiveController {

    private final TransactionRecordRepository transactionRecordRepository;

    public IncentiveController(TransactionRecordRepository transactionRecordRepository) {
        this.transactionRecordRepository = transactionRecordRepository;
    }

    @GetMapping("/{id}")
    public TransactionRecord getTransaction(@PathVariable Long id) {
        return transactionRecordRepository.findById(id).orElse(null);
    }
}
