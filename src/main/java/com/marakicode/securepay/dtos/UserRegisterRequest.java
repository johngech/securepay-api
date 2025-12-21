package com.marakicode.securepay.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRegisterRequest(
        @NotNull(message = "firstName is required")
        @Size(min = 3, max = 100, message = "Length must be between {min} and {max} characters.")
        String firstName,

        @NotNull(message = "lastName is required")
        @Size(min = 3, max = 100, message = "Length must be between {min} and {max} characters.")
        String lastName,

        @NotNull(message = "phone is required")
        @Size(min = 10, max = 15, message = "lastName must be between {min} to {max} characters long.")
        String phone,

        @NotNull(message = "email is required") @Email(message = "email must be valid")
        @Size(max = 255, message = "Length must be a maximum of {max} characters.")
        String email,

        @NotNull(message = "password is required")
        @Size(min = 6, max = 25, message = "Length must be between {min} and {max} characters.")
        String password) {
}
