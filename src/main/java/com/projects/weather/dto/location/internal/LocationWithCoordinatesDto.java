package com.projects.weather.dto.location.internal;

import lombok.Builder;

@Builder
public record LocationWithCoordinatesDto(Long id,
                                         String name,
                                         Double latitude,
                                         Double longitude) {
}
