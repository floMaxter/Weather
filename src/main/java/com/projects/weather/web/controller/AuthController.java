package com.projects.weather.web.controller;

import com.projects.weather.dto.request.LoginRequestDto;
import com.projects.weather.dto.request.RegisterRequestDto;
import com.projects.weather.service.AuthService;
import com.projects.weather.util.SessionCookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    private final SessionCookieUtils sessionCookieUtils;

    @Autowired
    public AuthController(AuthService authService, SessionCookieUtils sessionCookieUtils) {
        this.authService = authService;
        this.sessionCookieUtils = sessionCookieUtils;
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequestDto("", ""));
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("user") LoginRequestDto loginRequestDto, HttpServletResponse resp) {
        var sessionId = authService.login(loginRequestDto);
        sessionCookieUtils.setSessionCookie(resp, sessionId);

        return "redirect:/weather";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest req, HttpServletResponse resp) {
        sessionCookieUtils.getSessionCookie(req).ifPresent(cookie ->
                authService.logout(UUID.fromString(cookie.getValue())));
        sessionCookieUtils.expireSessionCookie(resp);

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
