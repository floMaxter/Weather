package com.projects.weather.dto.user.response;

import com.projects.weather.dto.location.internal.LocationWithCoordinatesDto;

import java.util.List;


public record UserWithLocationsDto(String login,
                                   List<LocationWithCoordinatesDto> locations) {
}
