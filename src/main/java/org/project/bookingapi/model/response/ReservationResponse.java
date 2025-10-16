package org.project.bookingapi.model.response;

import lombok.Builder;
import org.project.bookingapi.enums.BookingStatusType;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record ReservationResponse(
        UUID id,
        UUID userId,
        UUID roomId,
        String firstname,
        String surname,
        Long roomNumber,
        LocalDate checkinDate,
        LocalDate checkoutDate,
        BookingStatusType status
) {
}
