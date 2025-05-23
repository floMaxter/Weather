package com.projects.weather.dto.response;

import com.projects.weather.dto.LocationDto;

import java.util.List;

public record UserResponseDto(String login,
                              String password,
                              List<LocationDto> locations) {
}
