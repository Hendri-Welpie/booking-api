package org.project.bookingapi.repository;

import org.project.bookingapi.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Reservation, UUID> {
    @Query("""
              select r from reservation r\s
              where r.roomId = :roomId\s
                and r.status <> 'CANCELLED'
                and not (r.checkoutDate <= :checkin or r.checkinDate >= :checkout)
            """)
    List<Reservation> findOverlappingReservations(@Param("roomId") UUID roomId,
                                                  @Param("checkin") LocalDate checkin,
                                                  @Param("checkout") LocalDate checkout);

    Page<Reservation> findAllByUserId(UUID userId, Pageable pageable);
}