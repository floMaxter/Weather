package com.projects.weather.service.integration.service;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.projects.weather.client.OpenWeatherClient;
import com.projects.weather.dto.WeatherDataResponseDto;
import com.projects.weather.exception.OpenWeatherClientException;
import com.projects.weather.service.config.TestAppConfig;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringJUnitConfig(classes = TestAppConfig.class)
@WireMockTest
public class OpenWeatherClientIT {

    @RegisterExtension
    static WireMockExtension wireMockExtension = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void setUpMockBaseUrl(DynamicPropertyRegistry registry) {
        registry.add("api.weather.base_url", wireMockExtension::baseUrl);
    }

    private final OpenWeatherClient openWeatherClient;

    @Value("${api.weather.api_key}")
    private String apiKey;
    @Value("${api.weather.units_of_measurement}")
    private String unitsOfMeasurement;

    @AfterEach
    void clear() {
        wireMockExtension.resetAll();
    }

    @Test
    void findAllLocationsByName_whenValidName_ShouldReturnLocations() {
        // given
        var mockResponse = """
                [
                  {
                    "name": "Moscow",
                    "local_names": {
                      "en": "Moscow",
                      "ru": "Москва"
                    },
                    "lat": 46.7323875,
                    "lon": -117.0001651,
                    "country": "US",
                    "state": "Idaho"
                  },
                  {
                    "name": "Moscow",
                    "lat": 45.071096,
                    "lon": -69.891586,
                    "country": "US",
                    "state": "Maine"
                  },
                  {
                    "name": "Moscow",
                    "lat": 35.0619984,
                    "lon": -89.4039612,
                    "country": "US",
                    "state": "Tennessee"
                  }
                ]
                """;

        var locationName = "Moscow";
        var limit = 3;
        wireMockExtension.stubFor(
                get(urlPathEqualTo("/geo/1.0/direct"))
                        .withQueryParam("q", equalTo(locationName))
                        .withQueryParam("limit", equalTo(String.valueOf(limit)))
                        .withQueryParam("appid", equalTo(apiKey))
                        .willReturn(aResponse()
                                .withStatus(HttpServletResponse.SC_OK)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(mockResponse))
        );

        // when
        var resultLocations = openWeatherClient.findAllLocationsByName(locationName, limit);

        // then
        assertThat(resultLocations).hasSize(limit);
        assertThat(resultLocations.getFirst().name()).isEqualTo(locationName);
    }

    @Test
    void findAllLocationsByName_whenApiReturnEmpty_ShouldReturnEmptyList() {
        // given
        var locationName = "Nowhere";
        var limit = 3;
        wireMockExtension.stubFor(
                get(urlPathEqualTo("/geo/1.0/direct"))
                        .withQueryParam("q", equalTo(locationName))
                        .withQueryParam("limit", equalTo(String.valueOf(limit)))
                        .withQueryParam("appid", equalTo(apiKey))
                        .willReturn(aResponse()
                                .withStatus(HttpServletResponse.SC_OK)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody("[]"))
        );

        // when
        var resultLocations = openWeatherClient.findAllLocationsByName(locationName, limit);

        // then
        assertThat(resultLocations).isEmpty();
    }

    @Test
    void findAllLocationsByName_whenEmptyName_ShouldThrowException() {
        // given
        var locationName = "Invalid name";
        var limit = 3;
        var errorResponseMessage = """
                {
                  "cod": "400",
                  "message": "Nothing to geocode"
                }
                """;
        wireMockExtension.stubFor(
                get(urlPathEqualTo("/geo/1.0/direct"))
                        .withQueryParam("q", equalTo(locationName))
                        .withQueryParam("limit", equalTo(String.valueOf(limit)))
                        .withQueryParam("appid", equalTo(apiKey))
                        .willReturn(aResponse()
                                .withStatus(HttpServletResponse.SC_BAD_REQUEST)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(errorResponseMessage))
        );

        // when

        // then
        assertThatThrownBy(() -> openWeatherClient.findAllLocationsByName(locationName, limit))
                .isInstanceOf(OpenWeatherClientException.class);
    }

    @Test
    void findAllLocationsByName_whenServerError_ShouldThrowException() {
        // given
        var locationName = "Invalid name";
        var limit = 3;
        wireMockExtension.stubFor(
                get(urlPathEqualTo("/geo/1.0/direct"))
                        .withQueryParam("q", equalTo(locationName))
                        .withQueryParam("limit", equalTo(String.valueOf(limit)))
                        .withQueryParam("appid", equalTo(apiKey))
                        .willReturn(aResponse()
                                .withStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR))
        );

        // when

        // then
        assertThatThrownBy(() -> openWeatherClient.findAllLocationsByName(locationName, limit))
                .isInstanceOf(OpenWeatherClientException.class);
    }

    @Test
    void findWeatherDataByCoordinates_whenValidCoordinates_ShouldReturnWeatherDataResponseDto() {
        // given
        var longitude = 59.5;
        var latitude = 30.5;
        var mockResponse = """
                {
                   "coord": {
                     "lon": 59.5,
                     "lat": 30.5
                   },
                   "weather": [
                     {
                       "id": 800,
                       "main": "Clear",
                       "description": "clear sky",
                       "icon": "01d"
                     }
                   ],
                   "base": "stations",
                   "main": {
                     "temp": 39.71,
                     "feels_like": 36.08,
                     "temp_min": 39.71,
                     "temp_max": 39.71,
                     "pressure": 1004,
                     "humidity": 5,
                     "sea_level": 1004,
                     "grnd_level": 908
                   },
                   "visibility": 10000,
                   "wind": {
                     "speed": 8.51,
                     "deg": 209,
                     "gust": 7.94
                   },
                   "clouds": {
                     "all": 6
                   },
                   "dt": 1745921032,
                   "sys": {
                     "sunrise": 1745889614,
                     "sunset": 1745937495
                   },
                   "timezone": 12600,
                   "id": 0,
                   "name": "",
                   "cod": 200
                 }
                """;

        var expectedWeatherDataResponseDto = new WeatherDataResponseDto(
                List.of(new WeatherDataResponseDto.WeatherInfo("Clear", "clear sky")),
                new WeatherDataResponseDto.TemperatureInfo(39.71, 36.08, 5.0)
        );

        wireMockExtension.stubFor(
                get(urlPathEqualTo("/data/2.5/weather"))
                        .withQueryParam("lat", equalTo(String.valueOf(latitude)))
                        .withQueryParam("lon", equalTo(String.valueOf(longitude)))
                        .withQueryParam("appid", equalTo(apiKey))
                        .withQueryParam("units", equalTo(unitsOfMeasurement))
                        .willReturn(aResponse()
                                .withStatus(HttpServletResponse.SC_OK)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(mockResponse))
        );

        // when
        var resultWeather = openWeatherClient.findWeatherDataByCoordinates(latitude, longitude);

        // then
        assertThat(resultWeather).isEqualTo(expectedWeatherDataResponseDto);
    }

    @Test
    void findWeatherDataByCoordinates_whenInvalidCoordinates_ShouldThrowException() {
        // given
        var longitude = -10000.0;
        var latitude = -10000.0;
        var errorResponseMessage = """
                {
                  "cod": "400",
                  "message": "wrong latitude"
                }
                """;
        wireMockExtension.stubFor(
                get(urlPathEqualTo("/data/2.5/weather"))
                        .withQueryParam("lat", equalTo(String.valueOf(latitude)))
                        .withQueryParam("lon", equalTo(String.valueOf(longitude)))
                        .withQueryParam("appid", equalTo(apiKey))
                        .withQueryParam("units", equalTo(unitsOfMeasurement))
                        .willReturn(aResponse()
                                .withStatus(HttpServletResponse.SC_BAD_REQUEST)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(errorResponseMessage))
        );

        // when

        // then
        assertThatThrownBy(() -> openWeatherClient.findWeatherDataByCoordinates(latitude, longitude))
                .isInstanceOf(OpenWeatherClientException.class);
    }

    @Test
    void findWeatherDataByCoordinates_whenApiReturnsNullBody_ShouldThrowException() {
        // given
        var longitude = 0.0;
        var latitude = 0.0;

        wireMockExtension.stubFor(
                get(urlPathEqualTo("/data/2.5/weather"))
                        .withQueryParam("lat", equalTo(String.valueOf(latitude)))
                        .withQueryParam("lon", equalTo(String.valueOf(longitude)))
                        .withQueryParam("appid", equalTo(apiKey))
                        .withQueryParam("units", equalTo(unitsOfMeasurement))
                        .willReturn(aResponse()
                                .withStatus(HttpServletResponse.SC_OK)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(""))
        );

        // when

        // then
        assertThatThrownBy(() -> openWeatherClient.findWeatherDataByCoordinates(latitude, longitude))
                .isInstanceOf(OpenWeatherClientException.class);
    }

    @Test
    void findWeatherDataByCoordinates_whenServerError_ShouldThrowException() {
        // given
        var longitude = 0.0;
        var latitude = 0.0;

        wireMockExtension.stubFor(
                get(urlPathEqualTo("/data/2.5/weather"))
                        .withQueryParam("lat", equalTo(String.valueOf(latitude)))
                        .withQueryParam("lon", equalTo(String.valueOf(longitude)))
                        .withQueryParam("appid", equalTo(apiKey))
                        .withQueryParam("units", equalTo(unitsOfMeasurement))
                        .willReturn(aResponse()
                                .withStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR))
        );

        // when

        // then
        assertThatThrownBy(() -> openWeatherClient.findWeatherDataByCoordinates(latitude, longitude))
                .isInstanceOf(OpenWeatherClientException.class);
    }
}
