package com.marakicode.securepay.users;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChangePinRequest(
        String oldPin,

        @NotNull(message = "Pin is required")
        @Size(min = 6, max = 10, message = "Length must be between {min} and {max} characters long.")
        String newPin
) {
}
