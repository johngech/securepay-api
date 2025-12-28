package com.marakicode.securepay.payments;

public class PaymentException extends RuntimeException {
    public PaymentException(String message) {
        super(message);
    }
}
