package com.midas.core.service;

import com.midas.core.model.Incentive;
import com.midas.core.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IncentiveClient {

    private static final Logger log = LoggerFactory.getLogger(IncentiveClient.class);

    private final RestTemplate restTemplate;
    private final String incentiveEndpoint;

    public IncentiveClient(RestTemplate restTemplate,
                           @Value("${midas.incentive.base-url:http://localhost:8080}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.incentiveEndpoint = baseUrl.endsWith("/") ? baseUrl + "incentive" : baseUrl + "/incentive";
    }

    public double fetchIncentive(Transaction transaction) {
        try {
            Incentive incentive = restTemplate.postForObject(incentiveEndpoint, transaction, Incentive.class);
            if (incentive == null || incentive.getAmount() < 0) {
                return 0d;
            }
            return incentive.getAmount();
        } catch (Exception ex) {
            log.warn("Unable to retrieve incentive for transaction: {}", transaction, ex);
            return 0d;
        }
    }
}

