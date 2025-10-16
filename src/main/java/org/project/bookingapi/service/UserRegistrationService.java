package org.project.bookingapi.service;

import static org.springframework.http.HttpStatus.CONFLICT;

import jakarta.transaction.Transactional;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.project.bookingapi.entity.Users;
import org.project.bookingapi.exception.ValidationException;
import org.project.bookingapi.repository.UsersRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final UsersRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Users registerUser(Users user) {
        final var errors = new HashMap<String, String>();

        if (userRepository.existsByEmail(user.getEmail())) {
            errors.put("email", "Email [%s] is already taken".formatted(user.getEmail()));
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            errors.put("username", "Username [%s] is already taken".formatted(user.getUsername()));
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(CONFLICT, errors);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

}