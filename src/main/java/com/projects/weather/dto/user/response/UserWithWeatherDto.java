package com.projects.weather.dto.user.response;

import com.projects.weather.dto.location.response.LocationWithWeatherDto;
import lombok.Builder;

import java.util.List;

@Builder
public record UserWithWeatherDto(String login,
                                 List<LocationWithWeatherDto> locations) {
}
