package org.project.bookingapi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.bookingapi.model.request.AuthenticationRequestDto;
import org.project.bookingapi.model.response.AuthenticationResponseDto;
import org.project.bookingapi.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDto> authenticate(@RequestBody final AuthenticationRequestDto authenticationRequestDto) {
        log.info(authenticationRequestDto.toString());
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequestDto));
    }
}