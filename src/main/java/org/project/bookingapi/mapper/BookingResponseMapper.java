package org.project.bookingapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.project.bookingapi.entity.Reservation;
import org.project.bookingapi.model.response.ReservationResponse;

@Mapper
public interface BookingResponseMapper {
    BookingResponseMapper INSTANCE = Mappers.getMapper(BookingResponseMapper.class);

    @Mapping(target = "surname", source = "lastName")
    @Mapping(target = "firstname", source = "firstName")
    ReservationResponse map(Reservation reservation);
}
