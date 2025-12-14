package com.marakicode.securepay.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record UserUpdateRequest(
        @NotNull(message = "firstName is required")
        @Size(min = 3, max = 100, message = "Length must be between {min} and {max} characters.")
        String firstName,

        @NotNull(message = "lastName is required")
        @Size(min = 3, max = 100, message = "Length must be between {min} and {max} characters.")
        String lastName
) {
}
