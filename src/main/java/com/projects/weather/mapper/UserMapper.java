package com.projects.weather.mapper;

import com.projects.weather.dto.UserDto;
import com.projects.weather.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper extends Mappable<User, UserDto> {
}
