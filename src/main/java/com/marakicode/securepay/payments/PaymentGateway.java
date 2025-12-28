package com.marakicode.securepay.payments;

import java.util.Optional;

public interface PaymentGateway {
    PaymentSession createPaymentSession(PaymentData request);
    Optional<PaymentResult> parseWebhookRequest(WebhookRequest request);
    PaymentProviderType provider();
}
