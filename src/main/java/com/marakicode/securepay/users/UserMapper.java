package com.marakicode.securepay.users;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    User toEntity(UserRegisterRequest request);

    void update(UserUpdateRequest request, @MappingTarget User user);
}
