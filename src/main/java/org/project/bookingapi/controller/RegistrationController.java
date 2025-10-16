package org.project.bookingapi.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.project.bookingapi.mapper.UserRegistrationMapper;
import org.project.bookingapi.model.request.RegistrationRequestDto;
import org.project.bookingapi.model.response.RegistrationResponseDto;
import org.project.bookingapi.service.UserRegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class RegistrationController {

    private final UserRegistrationService userRegistrationService;

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponseDto> registerUser(@Valid @RequestBody final RegistrationRequestDto registrationDTO) {
        final var registeredUser = userRegistrationService.registerUser(UserRegistrationMapper.INSTANCE.map(registrationDTO));

        return ResponseEntity.ok(UserRegistrationMapper.INSTANCE.map(registeredUser));
    }

}