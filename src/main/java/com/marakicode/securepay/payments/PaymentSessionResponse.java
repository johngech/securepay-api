package com.marakicode.securepay.payments;

import com.marakicode.securepay.transactions.TransactionStatus;

import java.util.Map;

public record PaymentSessionResponse(
        Long transactionId,
        PaymentProviderType provider,
        PaymentSessionType sessionType,
        TransactionStatus status,
        String externalRef,
        Map<String, String> nextAction
) {
}
