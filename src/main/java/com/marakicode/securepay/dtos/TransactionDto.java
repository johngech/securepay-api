package com.marakicode.securepay.dtos;

import com.marakicode.securepay.entities.PaymentProvider;
import com.marakicode.securepay.entities.TransactionStatus;
import com.marakicode.securepay.entities.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class TransactionDto {
    private Long id;
    private String transactionCode;
    private String provider;
    private BigDecimal amount;
    private TransactionType type;
    private TransactionStatus status;
    private String description;
    private LocalDateTime createdAt;
}
