package com.projects.weather.interceptor;

import com.projects.weather.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthService authService;
    private final String sessionCookieName;

    @Autowired
    public AuthInterceptor(@Lazy AuthService authService,
                           @Value("${session.session_cookie_name}") String sessionCookieName) {
        this.authService = authService;
        this.sessionCookieName = sessionCookieName;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        getSessionCookie(request).ifPresent(sessionCookie -> {
            var sessionId = UUID.fromString(sessionCookie.getValue());
            authService.findAuthorizedUserBySessionId(sessionId).ifPresent(authorizedUserDto ->
                    request.setAttribute("authorizedUserDto", authorizedUserDto));
        });

        return true;
    }

    private Optional<Cookie> getSessionCookie(HttpServletRequest request) {
        var cookies = request.getCookies();
        if (cookies != null) {
            return Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals(sessionCookieName))
                    .findAny();
        }
        return Optional.empty();
    }
}
