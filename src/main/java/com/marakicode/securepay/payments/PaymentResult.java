package com.marakicode.securepay.payments;

import com.marakicode.securepay.transactions.TransactionStatus;

import java.math.BigDecimal;

public record PaymentResult(
        Long transactionId,
        TransactionStatus status,
        BigDecimal amount,
        String externalRef
) {
}
