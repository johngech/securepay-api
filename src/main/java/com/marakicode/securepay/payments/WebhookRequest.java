package com.marakicode.securepay.payments;

import java.util.Map;

public record WebhookRequest(
        Map<String, String> headers,
        String payload
) {
}
