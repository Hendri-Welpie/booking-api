package org.project.bookingapi.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.ToString;

public record RegistrationRequestDto(
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
        String username,

        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email address")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 30, message = "Password must be between 6 and 30 characters")
        String password,

        @NotBlank(message = "FirstName is required")
        @Size(min = 3, max = 20, message = "FirstName must be between 3 and 20 characters")
        String firstName,

        @NotBlank(message = "LastName is required")
        @Size(min = 3, max = 20, message = "FirstName must be between 3 and 20 characters")
        String lastName
) {

        @Override
        public String toString() {
                return "RegistrationRequestDto{" +
                        "username='" + username + '\'' +
                        ", email='" + email + '\'' +
                        ", password=[PROTECTED]" +
                        ", firstName='" + firstName + '\'' +
                        ", lastName='" + lastName + '\'' +
                        '}';
        }
}