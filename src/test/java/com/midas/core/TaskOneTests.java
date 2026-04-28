package com.midas.core;

import com.google.gson.Gson;
import com.midas.core.model.TransactionRecord;
import com.midas.core.model.User;
import com.midas.core.repository.TransactionRecordRepository;
import com.midas.core.repository.UserRepository;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Optional;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
public class TaskOneTests {

    @Container
    public static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @Autowired
    private UserRepository userRepository;

    private final Gson gson = new Gson();

    @BeforeEach
    void seedUsers() {
        userRepository.deleteAll();
        userRepository.save(new User(1L, "alpha", 1000.0));
        userRepository.save(new User(2L, "beta", 1000.0));
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        // H2 in-memory datasource is used from application.yml by default
    }

    @Test
    public void taskOne_flow_receives_kafka_and_saves_to_db() throws Exception {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        String payload = gson.toJson(new TestTransaction("task-one-123", 250.5, 1L, 2L));

        producer.send(new ProducerRecord<>("transactions", null, payload));
        producer.flush();
        producer.close();

        boolean found = false;
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < Duration.ofSeconds(20).toMillis()) {
            Optional<TransactionRecord> t = transactionRecordRepository.findByTxnId("task-one-123");
            if (t.isPresent()) {
                found = true;
                break;
            }
            Thread.sleep(500);
        }

        assertTrue(found, "Transaction produced to Kafka should be consumed and saved to DB");
    }

    private record TestTransaction(String txnId, double amount, Long senderId, Long recipientId) {}
}
