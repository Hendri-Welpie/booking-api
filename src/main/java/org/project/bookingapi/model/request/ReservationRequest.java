package org.project.bookingapi.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record ReservationRequest(
        UUID id,
        @NotNull UUID userId,
        @NotNull UUID roomId,
        String firstname,
        String surname,
        @NotNull Integer roomNum,
        @NotNull LocalDate checkinDate,
        @NotNull LocalDate checkoutDate
) {
}
