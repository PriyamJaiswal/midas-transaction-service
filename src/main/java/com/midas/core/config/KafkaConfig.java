package com.midas.core.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    private final String transactionsTopicName;

    public KafkaConfig(@Value("${midas.kafka.transactions-topic}") String transactionsTopicName) {
        this.transactionsTopicName = transactionsTopicName;
    }

    @Bean
    public NewTopic transactionsTopic() {
        return TopicBuilder.name(transactionsTopicName)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
