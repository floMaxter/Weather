package com.projects.weather.web.controller;

import com.projects.weather.dto.user.request.LoginRequestDto;
import com.projects.weather.dto.user.request.RegisterRequestDto;
import com.projects.weather.exception.EntityAlreadyExistsException;
import com.projects.weather.exception.InvalidPasswordException;
import com.projects.weather.exception.InvalidSessionException;
import com.projects.weather.exception.UserNotFoundException;
import com.projects.weather.service.AuthService;
import com.projects.weather.util.SessionCookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
    public String login(@Valid @ModelAttribute("loginRequest") LoginRequestDto loginRequestDto,
                        BindingResult bindingResult,
                        HttpServletResponse resp) {
        if (bindingResult.hasErrors()) {
            return "auth/login";
        }

        try {
            var sessionId = authService.login(loginRequestDto);
            sessionCookieUtils.setSessionCookie(resp, sessionId);
        } catch (UserNotFoundException ex) {
            bindingResult.rejectValue("login", "", "The user with this login was not found");
            return "auth/login";
        } catch (InvalidPasswordException ex) {
            bindingResult.rejectValue("password", "", ex.getMessage());
            return "auth/login";
        }

        return "redirect:/weather";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest req, HttpServletResponse resp) {
        var sessionCookie = sessionCookieUtils.getSessionCookie(req)
                .orElseThrow(() -> new InvalidSessionException("Session not found"));

        if (!sessionCookieUtils.isValidSessionId(sessionCookie.getValue())) {
            throw new InvalidSessionException("Invalid session id");
        }

        authService.logout(UUID.fromString(sessionCookie.getValue()));
        sessionCookieUtils.expireSessionCookie(resp);

        return "redirect:/auth/login";
    }

    @GetMapping("/register")
    public String registrationPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequestDto("", ""));
        return "auth/registration";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerRequest") RegisterRequestDto registerRequestDto,
                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "auth/registration";
        }

        try {
            authService.register(registerRequestDto);
        } catch (EntityAlreadyExistsException ex) {
            bindingResult.rejectValue("login", "", "This login is already taken");
            return "auth/registration";
        }

        return "redirect:/auth/login";
    }
}
