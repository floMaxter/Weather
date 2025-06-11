package com.projects.weather.web.controller;

import com.projects.weather.dto.location.request.CreateLocationRequestDto;
import com.projects.weather.dto.user.request.AuthorizedUserDto;
import com.projects.weather.exception.LocationAlreadyExistsException;
import com.projects.weather.service.LocationService;
import com.projects.weather.service.UserService;
import com.projects.weather.web.annotation.AuthorizedUser;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/locations")
@Validated
public class LocationController {

    private final UserService userService;
    private final LocationService locationService;

    public LocationController(UserService userService,
                              LocationService locationService) {
        this.userService = userService;
        this.locationService = locationService;
    }

    @GetMapping("/search")
    public String searchLocations(Model model,
                                  @AuthorizedUser @Nullable AuthorizedUserDto authorizedUserDto,
                                  @RequestParam("location") String locationName) {
        var locations = locationService.getLocationsByName(locationName);
        model.addAttribute("userDto", authorizedUserDto);
        model.addAttribute("locations", locations);

        return "locations/locations";
    }

    @PostMapping
    public String addLocation(@AuthorizedUser @Nullable AuthorizedUserDto authorizedUserDto,
                              @ModelAttribute CreateLocationRequestDto locationRequestDto) {
        if (authorizedUserDto == null) {
            return "redirect:/auth/login";
        }

        if (locationService.isLocationAlreadyAdded(authorizedUserDto.login(), locationRequestDto)) {
            throw new LocationAlreadyExistsException("This location has already been added to the weather list: " +
                                                     "name=" + locationRequestDto.name() + ", " +
                                                     "latitude=" + locationRequestDto.latitude() + ", " +
                                                     "longitude=" + locationRequestDto.longitude());
        }

        userService.addLocationToUser(authorizedUserDto.login(), locationRequestDto);

        return "redirect:/weather";
    }

    @DeleteMapping("/{id}")
    public String deleteLocation(@PathVariable("id") Long locationId,
                                 @AuthorizedUser @Nullable AuthorizedUserDto authorizedUserDto) {
        if (authorizedUserDto != null) {
            userService.removeLocationsFromUser(authorizedUserDto.login(), locationId);
        }

        return "redirect:/weather";
    }
}
