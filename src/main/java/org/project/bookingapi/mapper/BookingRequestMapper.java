package org.project.bookingapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.project.bookingapi.entity.Reservation;
import org.project.bookingapi.model.request.ReservationRequest;

@Mapper
public interface BookingRequestMapper {
    BookingRequestMapper INSTANCE = Mappers.getMapper(BookingRequestMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "lastName", source = "surname")
    @Mapping(target = "roomNumber", source = "roomNum")
    @Mapping(target = "firstName", source = "firstname")
    Reservation map(ReservationRequest request);
}
