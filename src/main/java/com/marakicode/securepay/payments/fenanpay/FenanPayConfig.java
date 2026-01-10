package com.marakicode.securepay.payments.fenanpay;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConfigurationProperties(prefix = "fenanpay")
@Data
public class FenanPayConfig {

    private String apiBaseUrl;
    private String apiKey;

    private String webhookSecretKey;
    private String secretKey;

    @Bean
    public RestTemplate fenanPayTemplate() {
        return new RestTemplate();
    }

    @Bean
    public FenanPayClient fenanPayClient(RestTemplate restTemplate) {
        return new FenanPayClient(restTemplate, apiBaseUrl, apiKey);
    }
}
