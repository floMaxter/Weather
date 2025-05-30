package com.projects.weather.web.controller;

import com.projects.weather.dto.location.request.CreateLocationRequestDto;
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

    private final UserService userService;
    private final LocationService locationService;

    public LocationController(UserService userService,
                              LocationService locationService) {
        this.userService = userService;
        this.locationService = locationService;
    }

    @GetMapping("/weather")
    public String getWeatherLocations(Model model,
                                      @AuthorizedUser @Nullable AuthorizedUserDto authorizedUserDto) {
        model.addAttribute("userDto", authorizedUserDto);

        if (authorizedUserDto != null) {
            var userWithLocationsDto = userService.findByLogin(authorizedUserDto.login());
            var locationsWithWeather = locationService.getWeatherForLocations(userWithLocationsDto.locations());
            model.addAttribute("locationsWithWeather", locationsWithWeather);
        }

        return "locations/user_weather_locations";
    }

    @GetMapping
    public String getLocations(Model model,
                               @AuthorizedUser @Nullable AuthorizedUserDto authorizedUserDto,
                               HttpServletRequest request) {
        model.addAttribute("userDto", authorizedUserDto);

        var locationName = request.getParameter("location");
        if (locationName != null) {
            var locations = locationService.getLocationsByName(locationName);
            model.addAttribute("locations", locations);
        }

        return "locations/locations";
    }

    @PostMapping
    public String addLocation(@AuthorizedUser @Nullable AuthorizedUserDto authorizedUserDto,
                              @ModelAttribute CreateLocationRequestDto locationRequestDto) {
        if (authorizedUserDto == null) {
            return "redirect:/auth/login";
        }

        userService.addLocationToUser(authorizedUserDto.login(), locationRequestDto);

        return "redirect:/locations/weather";
    }
}
