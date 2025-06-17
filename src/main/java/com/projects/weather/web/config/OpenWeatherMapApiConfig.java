package com.projects.weather.web.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestClient;

@Configuration
@PropertySource("classpath:application.properties")
public class OpenWeatherMapApiConfig {

    private static final Dotenv dotenv = Dotenv.load();

    @Bean
    public RestClient restClient(@Value("${open_weather_api.weather.base_url}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Bean
    public String openWeatherApiKey() {
        return dotenv.get("OPEN_WEATHER_API_KEY");
    }
}
