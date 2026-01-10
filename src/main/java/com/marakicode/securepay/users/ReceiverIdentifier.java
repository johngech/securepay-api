package com.marakicode.securepay.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

@ExactlyOneOf(fields = {"email", "phone"})
public record ReceiverIdentifier(
        @Email
        String email,

        @Pattern(
                regexp = "^\\+?[0-9]{9,15}$",
                message = "Invalid phone number"
        )
        String phone
) {
}
