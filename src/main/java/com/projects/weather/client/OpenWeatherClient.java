package com.projects.weather.client;


import com.projects.weather.dto.LocationResponseDto;
import com.projects.weather.dto.WeatherDataResponseDto;
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

    @Autowired
    public OpenWeatherClient(RestClient restClient,
                             String openWeatherApiKey,
                             @Value("${api.weather.units_of_measurement}") String unitsOfMeasurement) {
        this.restClient = restClient;
        this.openWeatherApiKey = openWeatherApiKey;
        this.unitsOfMeasurement = unitsOfMeasurement;
    }

    public List<LocationResponseDto> findAllLocationsByName(String name, int limit) {
        try {
            var locations = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/geo/1.0/direct")
                            .queryParam("q", name)
                            .queryParam("limit", limit)
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
                    .body(LocationResponseDto[].class);

            return locations != null ? Arrays.asList(locations) : Collections.emptyList();
        } catch (RestClientException e) {
            throw new OpenWeatherClientException("Failed to fetch locations from OpenWeather API", e);
        }
    }

    public WeatherDataResponseDto findWeatherDataByCoordinates(Double latitude, Double longitude) {
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
                    .body(WeatherDataResponseDto.class);

            if (response == null) {
                throw new OpenWeatherClientException("OpenWeather API returned empty response");
            }

            return response;
        } catch (RestClientException e) {
            throw new OpenWeatherClientException("Failed to fetch weather data from OpenWeather API", e);
        }
    }
}
