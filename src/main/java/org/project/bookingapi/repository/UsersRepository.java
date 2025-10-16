package org.project.bookingapi.repository;

import org.project.bookingapi.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsersRepository extends JpaRepository<Users, UUID> {
    Optional<Users> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
