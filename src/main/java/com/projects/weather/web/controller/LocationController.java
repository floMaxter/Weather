package com.projects.weather.web.controller;

import com.projects.weather.client.OpenWeatherClient;
import com.projects.weather.dto.location.internal.LocationWithCoordinatesDto;
import com.projects.weather.dto.user.request.AuthorizedUserDto;
import com.projects.weather.service.LocationService;
import com.projects.weather.service.UserService;
import com.projects.weather.web.annotation.AuthorizedUser;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/locations")
public class LocationController {

    private final OpenWeatherClient openWeatherClient;
    private final UserService userService;
    private final LocationService locationService;

    public LocationController(OpenWeatherClient openWeatherClient,
                              UserService userService,
                              LocationService locationService) {
        this.openWeatherClient = openWeatherClient;
        this.userService = userService;
        this.locationService = locationService;
    }

    @GetMapping("/weather")
    public String getWeatherLocations(Model model,
                                      @AuthorizedUser @Nullable AuthorizedUserDto authorizedUserDto) {
        if (authorizedUserDto != null) {
            var userWithLocationsDto = userService.findByLogin(authorizedUserDto.login());
            var locationsWithWeather = locationService.getWeatherForLocations(userWithLocationsDto);
            model.addAttribute("userDto", authorizedUserDto);
            model.addAttribute("locationsWithWeather", locationsWithWeather);
        }

        return "locations/user_weather_locations";
    }

    @GetMapping
    public String getLocations(Model model,
                               @AuthorizedUser @Nullable AuthorizedUserDto authorizedUserDto,
                               HttpServletRequest request) {
        var locationName = request.getParameter("location");
        var locations = openWeatherClient.findAllLocationsByName(locationName);
        model.addAttribute("locations", locations);
        model.addAttribute("userDto", authorizedUserDto);

        return "locations/locations";
    }

    @PostMapping
    public String addLocation(Model model,
                              @AuthorizedUser @Nullable AuthorizedUserDto authorizedUserDto,
                              @ModelAttribute LocationWithCoordinatesDto locationWithCoordinatesDto) {
        if (authorizedUserDto == null) {
            return "redirect:/auth/login";
        }

        userService.addLocationToUser(authorizedUserDto.login(), locationWithCoordinatesDto);

        return "redirect:/locations/weather";
    }
}
