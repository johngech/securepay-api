package com.marakicode.securepay.payments;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentProviderRepository extends JpaRepository<PaymentProvider, Integer> {
    Optional<PaymentProvider> findByName(PaymentProviders name);
}
