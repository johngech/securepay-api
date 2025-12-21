package com.marakicode.securepay.services;

import com.marakicode.securepay.entities.PaymentProvider;
import com.marakicode.securepay.entities.PaymentProviders;
import com.marakicode.securepay.repositories.PaymentProviderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class PaymentProviderService {
    private final PaymentProviderRepository providerRepository;

    public PaymentProvider getPaymentProvider() {
        return providerRepository.findByName(PaymentProviders.STRIPE)
                .orElseGet(() -> {
                    var newProvider = new PaymentProvider();
                    newProvider.setName(PaymentProviders.STRIPE);
                    return providerRepository.save(newProvider);
                });
    }
}
