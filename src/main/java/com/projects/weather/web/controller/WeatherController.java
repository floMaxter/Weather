package com.projects.weather.web.controller;

import com.projects.weather.dto.user.response.AuthorizedUserDto;
import com.projects.weather.service.LocationService;
import com.projects.weather.web.annotation.AuthorizedUser;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/weather")
public class WeatherController {

    private final LocationService locationService;

    @Autowired
    public WeatherController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public String getWeather(Model model,
                             @AuthorizedUser @Nullable AuthorizedUserDto authorizedUserDto) {
        model.addAttribute("userDto", authorizedUserDto);

        if (authorizedUserDto != null) {
            var locationsWithWeather = locationService.getLocationsWithWeatherByUserLogin(authorizedUserDto.login());
            model.addAttribute("locationsWithWeather", locationsWithWeather);
        }

        return "locations/user_weather_locations";
    }
}
