package com.marakicode.securepay.payments.fenanpay;

import java.math.BigDecimal;

public record FenanPayRequest(
        Long senderId,
        Long receiverId,
        BigDecimal amount,
        String externalRef
) {
}
