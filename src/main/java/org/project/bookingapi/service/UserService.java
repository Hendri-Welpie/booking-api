package org.project.bookingapi.service;

import lombok.RequiredArgsConstructor;
import org.project.bookingapi.mapper.UserMapper;
import org.project.bookingapi.model.UserProfileDto;
import org.project.bookingapi.repository.UsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.GONE;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UsersRepository userRepository;

    public UserProfileDto getUserByUsername(final String username) {
        return UserMapper.INSTANCE.map(userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(GONE, "The user account has been deleted or inactivated")));
    }
}