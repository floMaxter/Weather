package com.projects.weather.controller;

import com.projects.weather.dto.UserDto;
import com.projects.weather.dto.response.UserResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;

@Controller
@RequestMapping("/weather")
public class MainController {

    @GetMapping
    public String home(Model model, HttpServletRequest request) {
        var authorizedUserDto = request.getAttribute("authorizedUserDto");
        if (authorizedUserDto != null) {
            var userDto = (UserResponseDto) authorizedUserDto;
            model.addAttribute("userDto", userDto);
        } else {
            // logic for unauthorized user
            model.addAttribute("userDto", new UserDto(null, "", "", Collections.emptyList()));
        }

        return "main_page";
    }
}
