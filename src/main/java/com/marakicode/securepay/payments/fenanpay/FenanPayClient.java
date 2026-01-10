package com.marakicode.securepay.payments.fenanpay;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class FenanPayClient {
    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiKey;

    public FenanPayResponse transfer(FenanPayRequest request) {
        try {
            var headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);
            headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            Map<String, Object> body = new HashMap<>();
            body.put("senderId", request.senderId());
            body.put("receiverId", request.receiverId());
            body.put("amount", request.amount());
            body.put("reference", request.externalRef());

            var requestEntity = new HttpEntity<>(body, headers);

            return restTemplate.postForObject(baseUrl, requestEntity, FenanPayResponse.class);
        } catch (Exception e) {
            return new FenanPayResponse(false, e.getMessage(), null);
        }
    }

}
