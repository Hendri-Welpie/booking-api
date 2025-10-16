package org.project.bookingapi.model.response;

public record RegistrationResponseDto(
        String username,
        String email
) {
}