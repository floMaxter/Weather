package com.projects.weather.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record UserDto(Long id,
                      String login,
                      String password,
                      List<LocationDto> locations) {
}
