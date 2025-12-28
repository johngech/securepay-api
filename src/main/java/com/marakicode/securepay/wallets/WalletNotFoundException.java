package com.marakicode.securepay.wallets;

public class WalletNotFoundException extends RuntimeException {
    public WalletNotFoundException() {
        super("Wallet not found");
    }

    public WalletNotFoundException(String message) {
        super(message);
    }
}
