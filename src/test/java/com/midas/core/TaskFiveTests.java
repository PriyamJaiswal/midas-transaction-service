package com.midas.core;

import com.google.gson.Gson;
import com.midas.core.dto.Balance;
import com.midas.core.repository.UserRepository;
import com.midas.core.support.UserTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext
public class TaskFiveTests {

    @Autowired
    private UserRepository userRepository;

    private final Gson gson = new Gson();

    @BeforeEach
    void seedUsers() {
        UserTestData.seedStandardUsers(userRepository);
    }

    @Test
    void task_five_balance_endpoint() {
        System.out.println("----- BEGIN TASK FIVE TEST OUTPUT -----");
        RestTemplate rt = new RestTemplate();

        String urlExisting = "http://localhost:33400/balance?userId=1";
        String jsonExisting = rt.getForObject(urlExisting, String.class);
        Balance bExisting = gson.fromJson(jsonExisting, Balance.class);
        System.out.println("Existing user response: " + jsonExisting);
        assertEquals(1200.23, bExisting.getBalance(), 0.0001);

        String urlMissing = "http://localhost:33400/balance?userId=9999";
        String jsonMissing = rt.getForObject(urlMissing, String.class);
        Balance bMissing = gson.fromJson(jsonMissing, Balance.class);
        System.out.println("Missing user response: " + jsonMissing);
        assertEquals(0.0, bMissing.getBalance(), 0.0001);

        System.out.println("----- END TASK FIVE TEST OUTPUT -----");
    }
}

