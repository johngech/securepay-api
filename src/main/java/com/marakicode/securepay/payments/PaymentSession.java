package com.marakicode.securepay.payments;

import java.util.Map;

public record PaymentSession(
        PaymentProviderType provider,
        PaymentSessionType sessionType,
        Map<String, String> data
) {
}
