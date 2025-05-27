package com.projects.weather.web.controller;

import com.projects.weather.dto.response.AuthorizedUserDto;
import com.projects.weather.util.SessionCookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/weather")
public class MainController {

    private final SessionCookieUtils sessionCookieUtils;

    public MainController(SessionCookieUtils sessionCookieUtils) {
        this.sessionCookieUtils = sessionCookieUtils;
    }

    @GetMapping
    public String showUserLocations(Model model, HttpServletRequest request) {
        getAuthorizedUserDtoFromRequest(request)
                .ifPresent(authorizedUserDto -> model.addAttribute("userDto", authorizedUserDto));

        return "locations/user_locations";
    }

    @GetMapping("/search")
    public String searchLocations(Model model, HttpServletRequest request) {

        return "locations/search_locations";
    }

    @PostMapping
    public String searchLocations(Model model) {

        return "redirect:/weather/search";
    }

    private Optional<AuthorizedUserDto> getAuthorizedUserDtoFromRequest(HttpServletRequest request) {
        var authorizedUserDto = request.getAttribute(sessionCookieUtils.getAuthorizedUserAttribute());
        if (authorizedUserDto != null) {
            try {
                var userDto = (AuthorizedUserDto) authorizedUserDto;
                return Optional.of(userDto);
            } catch (ClassCastException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}
