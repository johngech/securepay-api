package com.marakicode.securepay.wallets;

public class InvalidPinException extends RuntimeException {
    public InvalidPinException() {
        super("Invalid pin.");
    }

    public InvalidPinException(String message) {
        super(message);
    }
}
