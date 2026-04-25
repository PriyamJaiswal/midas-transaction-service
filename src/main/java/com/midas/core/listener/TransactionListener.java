package com.midas.core.listener;

import com.google.gson.Gson;
import com.midas.core.dto.TransactionDto;
import com.midas.core.service.TransactionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TransactionListener {

    private final TransactionService service;
    private final Gson gson = new Gson();
    private final String topic;

    public TransactionListener(TransactionService service,
                               @Value("${midas.kafka.transactions-topic}") String topic) {
        this.service = service;
        this.topic = topic;
    }

    @KafkaListener(topics = "${midas.kafka.transactions-topic}")
    public void listen(String message) {
        TransactionDto dto = gson.fromJson(message, TransactionDto.class);
        System.out.println("Received from Kafka on topic '" + topic + "': " + message);
        service.processIncomingTransaction(dto);
    }
}
