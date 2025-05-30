package com.projects.weather.client;


import com.projects.weather.dto.external.openweather.CurrentWeatherResponseDto;
import com.projects.weather.dto.external.openweather.LocationSearchResponseDto;
import com.projects.weather.exception.OpenWeatherClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class OpenWeatherClient {

    private final RestClient restClient;
    private final String openWeatherApiKey;
    private final String unitsOfMeasurement;
    private final int locationSearchLimit;

    @Autowired
    public OpenWeatherClient(RestClient restClient,
                             String openWeatherApiKey,
                             @Value("${open_weather_api.weather.units_of_measurement}") String unitsOfMeasurement,
                             @Value("${open_weather_api.location.search_limit}") int locationSearchLimit) {
        this.restClient = restClient;
        this.openWeatherApiKey = openWeatherApiKey;
        this.unitsOfMeasurement = unitsOfMeasurement;
        this.locationSearchLimit = locationSearchLimit;
    }

    public List<LocationSearchResponseDto> findAllLocationsByName(String name) {
        try {
            var locations = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/geo/1.0/direct")
                            .queryParam("q", name)
                            .queryParam("limit", locationSearchLimit)
                            .queryParam("appid", openWeatherApiKey)
                            .build())
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, ((req, resp) -> {
                        throw new OpenWeatherClientException("Client error while fetching locations: "
                                                             + resp.getStatusCode() + " - " + resp.getStatusText());
                    }))
                    .onStatus(HttpStatusCode::is5xxServerError, ((req, resp) -> {
                        throw new OpenWeatherClientException("Server error from OpenWeather API: "
                                                             + resp.getStatusText() + " - " + resp.getStatusCode());
                    }))
                    .body(LocationSearchResponseDto[].class);

            return locations != null ? Arrays.asList(locations) : Collections.emptyList();
        } catch (RestClientException e) {
            throw new OpenWeatherClientException("Failed to fetch locations from OpenWeather API", e);
        }
    }

    public CurrentWeatherResponseDto findWeatherDataByCoordinates(Double latitude, Double longitude) {
        try {
            var response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("data/2.5/weather")
                            .queryParam("lat", latitude)
                            .queryParam("lon", longitude)
                            .queryParam("appid", openWeatherApiKey)
                            .queryParam("units", unitsOfMeasurement)
                            .build())
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, ((req, resp) -> {
                        throw new OpenWeatherClientException("Client error while fetching weather: "
                                                             + resp.getStatusCode() + " - " + resp.getStatusText());
                    }))
                    .onStatus(HttpStatusCode::is5xxServerError, ((req, resp) -> {
                        throw new OpenWeatherClientException("Server error from OpenWeather API: "
                                                             + resp.getStatusText() + " - " + resp.getStatusCode());
                    }))
                    .body(CurrentWeatherResponseDto.class);

            if (response == null) {
                throw new OpenWeatherClientException("OpenWeather API returned empty response");
            }

            return response;
        } catch (RestClientException e) {
            throw new OpenWeatherClientException("Failed to fetch weather data from OpenWeather API", e);
        }
    }
}
