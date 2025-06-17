package com.projects.weather.dto.location.response;

import lombok.Builder;

@Builder
public record LocationSearchReadDto(String name,
                                    Double longitude,
                                    Double latitude,
                                    String country,
                                    String state) {
}
