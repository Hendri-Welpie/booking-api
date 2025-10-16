package org.project.bookingapi.model;

import lombok.Builder;
import org.project.bookingapi.enums.RoomType;

import java.util.UUID;

@Builder
public record RoomsDto(
        UUID id,
        RoomType type,
        Long roomNumber) {
}