package org.project.bookingapi.service;

import lombok.RequiredArgsConstructor;
import org.project.bookingapi.model.request.AuthenticationRequestDto;
import org.project.bookingapi.model.response.AuthenticationResponseDto;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticationResponseDto authenticate(final AuthenticationRequestDto request) {
        final var authToken = UsernamePasswordAuthenticationToken.unauthenticated(request.username(), request.password());
        final var authentication = authenticationManager.authenticate(authToken);

        final var token = jwtService.generateToken(request.username());
        return new AuthenticationResponseDto(token);
    }
}