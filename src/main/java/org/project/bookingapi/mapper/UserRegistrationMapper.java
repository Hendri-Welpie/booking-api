package org.project.bookingapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.project.bookingapi.entity.Users;
import org.project.bookingapi.model.request.RegistrationRequestDto;
import org.project.bookingapi.model.response.RegistrationResponseDto;

@Mapper
public interface UserRegistrationMapper {
    UserRegistrationMapper INSTANCE = Mappers.getMapper(UserRegistrationMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Users map(RegistrationRequestDto registrationRequestDto);

    RegistrationResponseDto map(Users users);

}
