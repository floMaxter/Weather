package com.projects.weather.controller;

import com.projects.weather.dto.request.LoginRequestDto;
import com.projects.weather.dto.request.RegisterRequestDto;
import com.projects.weather.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private static final String USER_SESSION_ID = "USERSESSIONID";

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequestDto("", ""));
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("user") LoginRequestDto loginRequestDto, HttpServletResponse resp) {
        var sessionId = authService.login(loginRequestDto);
        var userSessionIdCookie = new Cookie(USER_SESSION_ID, sessionId.toString());
        userSessionIdCookie.setPath("/");
        resp.addCookie(userSessionIdCookie);

        return "redirect:/users";
    }

    @PostMapping("/logout")
    public String logout(@CookieValue(USER_SESSION_ID) String userSessionId,
                         HttpServletResponse resp) {
        authService.logout(UUID.fromString(userSessionId));
        var expiredCookie = new Cookie(USER_SESSION_ID, null);
        expiredCookie.setMaxAge(0);
        expiredCookie.setPath("/");
        resp.addCookie(expiredCookie);

        return "redirect:/auth/login";
    }

    @GetMapping("/register")
    public String registrationPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequestDto("", ""));
        return "auth/registration";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") RegisterRequestDto registerRequestDto) {
        authService.register(registerRequestDto);
        return "redirect:/auth/login";
    }
}
