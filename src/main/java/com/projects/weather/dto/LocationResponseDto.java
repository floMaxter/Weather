package com.projects.weather.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LocationResponseDto(@JsonProperty("name") String name,
                                  @JsonProperty("lon") Double longitude,
                                  @JsonProperty("lat") Double latitude,
                                  @JsonProperty("country") String country,
                                  @JsonProperty("state") String state) {
}
