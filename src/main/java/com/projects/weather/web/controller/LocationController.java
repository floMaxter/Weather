package com.projects.weather.web.controller;

import com.projects.weather.dto.location.request.CreateLocationRequestDto;
import com.projects.weather.dto.user.response.AuthorizedUserDto;
import com.projects.weather.exception.LocationAlreadyExistsException;
import com.projects.weather.service.LocationService;
import com.projects.weather.service.UserService;
import com.projects.weather.web.annotation.AuthorizedUser;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping("/search")
    public String searchLocations(@AuthorizedUser @Nullable AuthorizedUserDto authorizedUserDto,
                                  @RequestParam("location") String locationName,
                                  Model model) {
        model.addAttribute("userDto", authorizedUserDto);

        if (locationName == null || locationName.isBlank()) {
            model.addAttribute("locationNameSearchError", "The location name should not be empty");
            return "locations/locations";
        }

        var locations = locationService.getLocationsByName(locationName);
        model.addAttribute("locations", locations);

        return "locations/locations";
    }

    @PostMapping
    public String addLocation(@AuthorizedUser @Nullable AuthorizedUserDto authorizedUserDto,
                              @ModelAttribute CreateLocationRequestDto locationRequestDto,
                              RedirectAttributes redirectAttributes) {
        if (authorizedUserDto == null) {
            return "redirect:/auth/login";
        }

        try {
            userService.addLocationToUser(authorizedUserDto.login(), locationRequestDto);
        } catch (LocationAlreadyExistsException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            redirectAttributes.addAttribute("location", locationRequestDto.name());
            return "redirect:/locations/search";
        }

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
