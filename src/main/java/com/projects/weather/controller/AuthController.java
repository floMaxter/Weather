package com.projects.weather.controller;

import com.projects.weather.dto.UserDto;
import com.projects.weather.service.AuthService;
import com.projects.weather.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @Autowired
    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("user", new UserDto());
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("user") UserDto userDto, HttpServletResponse resp) {
        var sessionId = authService.login(userDto);
        var userSessionIdCookie = new Cookie("USERSESSIONID", sessionId.toString());
        userSessionIdCookie.setPath("/");
        resp.addCookie(userSessionIdCookie);

        return "redirect:/users";
    }

    @GetMapping("/register")
    public String registrationPage(Model model) {
        model.addAttribute("user", new UserDto());
        return "auth/registration";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") UserDto userDto) {
        userService.save(userDto);
        return "redirect:/auth/login";
    }
}
