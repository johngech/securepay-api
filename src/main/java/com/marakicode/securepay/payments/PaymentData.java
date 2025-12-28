package com.marakicode.securepay.payments;

import java.math.BigDecimal;

public record PaymentData(
        Long transactionId,
        String externalRef,
        BigDecimal amount
) {
}
