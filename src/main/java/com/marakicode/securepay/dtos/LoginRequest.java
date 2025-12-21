package com.marakicode.securepay.dtos;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotNull(message = "Email is required")
        @Email(message = "Must be valid email")
        String email,
        @NotNull(message = "Password is required")
        String password) {
}
