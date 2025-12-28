package com.marakicode.securepay.payments;

import com.marakicode.securepay.transactions.TransactionStatus;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.RequestOptions;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class StripePaymentGateway implements PaymentGateway {
    @Value("${stripe.webhookSecretKey}")
    private String webhookSecretKey;

    @Override
    public PaymentSession createPaymentSession(PaymentData data) {
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(data.amount().multiply(BigDecimal.valueOf(100)).longValue())
                    .setCurrency("usd")
                    .putMetadata("transactionId", data.transactionId().toString())
                    .putMetadata("externalRef", data.externalRef())
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    )
                    .build();

            var requestOptions = RequestOptions.builder()
                    .setIdempotencyKey(data.externalRef())
                    .build();

            PaymentIntent intent = PaymentIntent.create(params, requestOptions);

            return new PaymentSession(
                    provider(),
                    PaymentSessionType.CLIENT_SECRET,
                    Map.of("clientSecret", intent.getClientSecret())
            );

        } catch (StripeException ex) {
            throw new PaymentException(ex.getMessage());
        }
    }

    @Override
    public Optional<PaymentResult> parseWebhookRequest(WebhookRequest request) {
        try {
            var signature = request.headers().get("Stripe-Signature");
            var event = Webhook.constructEvent(
                    request.payload(),
                    signature,
                    webhookSecretKey
            );

            return switch (event.getType()) {
                case "payment_intent.succeeded" -> Optional.of(new PaymentResult(
                        extractTransactionId(event),
                        TransactionStatus.COMPLETED,
                        extractAmount(event),
                        extractIntentId(event))
                );

                case "payment_intent.payment_failed" -> Optional.of(new PaymentResult(
                        extractTransactionId(event),
                        TransactionStatus.FAILED,
                        extractAmount(event),
                        extractIntentId(event))
                );

                default -> Optional.empty();
            };

        } catch (SignatureVerificationException ex) {
            throw new PaymentException("Invalid stripe signature");
        }

    }

    private Long extractTransactionId(Event event) {
        var intent = paymentIntent(event);
        return Long.valueOf(intent.getMetadata().get("transactionId"));
    }

    private String extractIntentId(Event event) {
        var intent = paymentIntent(event);
        return intent.getId();
    }

    private BigDecimal extractAmount(Event event) {
        var intent = paymentIntent(event);

        return BigDecimal.valueOf(intent.getAmount())
                .divide(BigDecimal.valueOf(100)); // cents -> dollars
    }

    private PaymentIntent paymentIntent(Event event) {
        return (PaymentIntent) event.getDataObjectDeserializer()
                .getObject()
                .orElseThrow(() -> new PaymentException("Invalid payment data"));
    }

    @Override
    public PaymentProviderType provider() {
        return PaymentProviderType.STRIPE;
    }
}
