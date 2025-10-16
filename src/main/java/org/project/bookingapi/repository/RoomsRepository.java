package org.project.bookingapi.repository;

import jakarta.persistence.LockModeType;
import org.project.bookingapi.entity.Rooms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoomsRepository extends JpaRepository<Rooms, UUID> {
    Optional<Rooms> findByRoomNumber(Long roomNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Rooms> findById(final UUID id);
}