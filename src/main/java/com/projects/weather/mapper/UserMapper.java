package com.projects.weather.mapper;

import com.projects.weather.dto.UserDto;
import com.projects.weather.dto.request.RegisterRequestDto;
import com.projects.weather.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserDto userDto);

    List<User> toEntity(List<UserDto> userDtos);

    UserDto toDto(User user);

    List<UserDto> toDto(List<User> users);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "locations", ignore = true)
    User fromRegisterRequest(RegisterRequestDto dto);
}
