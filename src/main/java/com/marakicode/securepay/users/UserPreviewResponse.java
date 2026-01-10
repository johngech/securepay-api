package com.marakicode.securepay.users;

public record UserPreviewResponse(
        Long id,
        String fullName,
        String contact
) {
}
