package com.projects.weather.mapper;

import com.projects.weather.dto.request.CreateUserDto;
import com.projects.weather.dto.response.AuthorizedUserDto;
import com.projects.weather.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(CreateUserDto userDto);

    AuthorizedUserDto toDto(User user);
}
