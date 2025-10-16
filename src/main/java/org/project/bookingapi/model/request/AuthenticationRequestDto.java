package org.project.bookingapi.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.ToString;

public record AuthenticationRequestDto(
        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Password is required")
        String password
) {

        @Override
        public String toString() {
                return "AuthenticationRequestDto{" +
                        "username='" + username + '\'' +
                        ", password='[PROTECTED]'" +
                        '}';
        }
}