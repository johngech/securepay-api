package com.marakicode.securepay.payments;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class PaymentProviderService {
    private final PaymentProviderRepository providerRepository;

    public PaymentProvider getPaymentProvider() {
        return providerRepository.findByProviderType(PaymentProviderType.STRIPE)
                .orElseGet(() -> {
                    var newProvider = new PaymentProvider();
                    newProvider.setProviderType(PaymentProviderType.STRIPE);
                    return providerRepository.save(newProvider);
                });
    }
}
