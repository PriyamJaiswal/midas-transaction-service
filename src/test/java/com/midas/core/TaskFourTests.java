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
class TaskFourTests {

    private static final Logger log = LoggerFactory.getLogger(TaskFourTests.class);

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
    void task_four_verifier() throws Exception {
        List<String> payloads = loadTransactions("test_data/task_four_transactions.jsonl");
        for (String payload : payloads) {
            kafkaTemplate.send(topic, payload);
        }
        kafkaTemplate.flush();

        Thread.sleep(Duration.ofSeconds(5).toMillis());

        log.info("----------------------------------------------------------");
        log.info("All task four transactions have been published.");
        log.info("Attach a debugger and inspect the User entity for 'wilbur'.");
        log.info("Record wilbur's final balance (rounded down) after processing.");
        log.info("This test will now pause briefly.");
        Thread.sleep(Duration.ofSeconds(30).toMillis());
        log.info("TaskFourTests complete.");
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

