package com.projects.weather.dto.location.request;

import lombok.Builder;

@Builder
public record CreateLocationRequestDto(String name,
                                       Double latitude,
                                       Double longitude) {
}
