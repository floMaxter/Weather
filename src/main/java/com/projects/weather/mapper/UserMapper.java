package com.projects.weather.mapper;

import com.projects.weather.dto.user.response.UserWithLocationsDto;
import com.projects.weather.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserWithLocationsDto toUserWithLocationsDto(User user);
}
