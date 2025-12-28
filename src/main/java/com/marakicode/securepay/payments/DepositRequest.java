package com.marakicode.securepay.payments;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DepositRequest(
        @NotNull(message = "status is required.")
        @DecimalMin(value = "0.01", message = "Amount must be at least {value}.")
        BigDecimal amount
) {
}
