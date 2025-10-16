package org.project.bookingapi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.bookingapi.model.UserProfileDto;
import org.project.bookingapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserProfileDto> getUserProfile(final Authentication authentication) {
        log.info(authentication.getName());
        return ResponseEntity.ok(userService.getUserByUsername(authentication.getName()));
    }
}