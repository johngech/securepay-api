package com.marakicode.securepay.payments.fenanpay;

public record FenanPayResponse(
        boolean success,
        String message,
        String transactionId
) {
    public boolean isSuccessful() {
        return success;
    }
}

