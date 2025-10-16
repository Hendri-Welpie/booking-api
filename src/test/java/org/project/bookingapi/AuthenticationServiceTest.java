package org.project.bookingapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.bookingapi.model.request.AuthenticationRequestDto;
import org.project.bookingapi.model.response.AuthenticationResponseDto;
import org.project.bookingapi.service.AuthenticationService;
import org.project.bookingapi.service.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private AuthenticationRequestDto requestDto;
    private Authentication authentication;
    private final String mockToken = "mock_jwt_token";
    private final String username = "testuser";
    private final String password = "password";

    @BeforeEach
    void setUp() {
        requestDto = new AuthenticationRequestDto(username, password);
        authentication = new UsernamePasswordAuthenticationToken(username, password);
    }

    @Test
    void authenticate_withValidCredentials_returnsAuthenticationResponseDto() {
        // Arrange
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(authentication);
        given(jwtService.generateToken(username))
                .willReturn(mockToken);

        // Act
        AuthenticationResponseDto result = authenticationService.authenticate(requestDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.token()).isEqualTo(mockToken);

        // Verify that the dependencies were called correctly
        then(authenticationManager).should().authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.username(), requestDto.password()));
        then(jwtService).should().generateToken(requestDto.username());
    }

    @Test
    void authenticate_withInvalidCredentials_throwsBadCredentialsException() {
        // Arrange
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willThrow(new BadCredentialsException("Invalid username or password"));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authenticationService.authenticate(requestDto));

        // Verify that the authentication manager was called, but jwtService was not
        then(authenticationManager).should().authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.username(), requestDto.password()));
        then(jwtService).shouldHaveNoInteractions();
    }
}