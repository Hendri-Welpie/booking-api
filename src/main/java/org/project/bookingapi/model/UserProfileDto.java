package org.project.bookingapi.model;

import java.util.UUID;

public record UserProfileDto(UUID id, String firstName, String lastName, String email, String username) {
}
