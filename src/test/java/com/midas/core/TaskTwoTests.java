package com.midas.core;

import com.midas.core.repository.UserRepository;
import com.midas.core.support.UserTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(
        partitions = 1,
        topics = {"${midas.kafka.transactions-topic:transactions}"},
        brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"}
)
class TaskTwoTests {

    private static final Logger log = LoggerFactory.getLogger(TaskTwoTests.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private UserRepository userRepository;

    @Value("${midas.kafka.transactions-topic}")
    private String topic;

    @BeforeEach
    void seedUsers() {
        UserTestData.seedStandardUsers(userRepository);
    }

    @Test
    void task_two_verifier() throws Exception {
        List<String> payloads = loadTransactions("test_data/task_two_transactions.jsonl");
        for (String payload : payloads) {
            kafkaTemplate.send(topic, payload);
        }
        kafkaTemplate.flush();

        Thread.sleep(Duration.ofSeconds(5).toMillis());

        log.info("----------------------------------------------------------");
        log.info("Kafka payloads have been published. Attach a debugger to");
        log.info("TransactionListener#listen or TransactionService#processIncomingTransaction");
        log.info("and record the amount field for the first four transactions.");
        log.info("This test will pause for a short period to give you time.");
        Thread.sleep(Duration.ofSeconds(25).toMillis());
        log.info("TaskTwoTests complete.");
    }

    private List<String> loadTransactions(String path) throws IOException {
        Resource resource = new ClassPathResource(path);
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines()
                    .filter(line -> !line.isBlank())
                    .collect(Collectors.toList());
        }
    }
}

