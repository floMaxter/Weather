package com.projects.weather.dto.external.openweather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LocationSearchResponseDto(@JsonProperty("name") String name,
                                        @JsonProperty("lon") Double longitude,
                                        @JsonProperty("lat") Double latitude,
                                        @JsonProperty("country") String country,
                                        @JsonProperty("state") String state) {
}
