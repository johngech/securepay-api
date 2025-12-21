package com.marakicode.securepay.auth;

import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
@ConfigurationProperties(prefix = "spring.jwt")
@Data
public class JwtConfig {
    private String secreteKey;
    private long accessTokenExpiration;
    private long refreshTokenExpiration;

    public SecretKey getSecreteKey() {
        return Keys.hmacShaKeyFor(secreteKey.getBytes());
    }
}
