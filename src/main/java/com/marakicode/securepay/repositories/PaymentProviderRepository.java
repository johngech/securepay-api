package com.marakicode.securepay.repositories;

import com.marakicode.securepay.entities.PaymentProvider;
import com.marakicode.securepay.entities.PaymentProviders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentProviderRepository extends JpaRepository<PaymentProvider, Integer> {
    Optional<PaymentProvider> findByName(PaymentProviders name);
}
