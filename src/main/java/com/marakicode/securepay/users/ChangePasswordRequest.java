package com.marakicode.securepay.users;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotNull(message = "oldPassword is required")
        @Size(min = 6, max = 25, message = "Length must be between {min} and {max} characters.")
        String oldPassword,
        @NotNull(message = "newPassword is required")
        @Size(min = 6, max = 25, message = "Length must be between {min} and {max} characters.")
        String newPassword) {
}
