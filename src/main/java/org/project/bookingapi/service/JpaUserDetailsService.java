package org.project.bookingapi.service;

import lombok.RequiredArgsConstructor;
import org.project.bookingapi.repository.UsersRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {

    private final UsersRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String username) {
        return userRepository.findByUsername(username).map(user -> User.builder()
                .username(username)
                .password(user.getPassword())
                .build())
                .orElseThrow(() -> new UsernameNotFoundException("User with username [%s] not found".formatted(username)));
    }

}