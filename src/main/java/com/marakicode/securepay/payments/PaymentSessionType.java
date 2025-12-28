package com.marakicode.securepay.payments;

public enum PaymentSessionType {
    CLIENT_SECRET,   // Stripe.js / mobile SDK
    REDIRECT_URL,    // FenanPay / browser redirect
    NONE             // Bank / async / manual
}
