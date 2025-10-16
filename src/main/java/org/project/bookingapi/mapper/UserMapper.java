package org.project.bookingapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.project.bookingapi.entity.Users;
import org.project.bookingapi.model.UserProfileDto;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserProfileDto map(Users users);
}
