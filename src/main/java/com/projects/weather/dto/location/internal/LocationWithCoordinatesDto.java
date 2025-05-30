package com.projects.weather.dto.location.internal;

public record LocationWithCoordinatesDto(Long id,
                                         String name,
                                         Double latitude,
                                         Double longitude) {
}
