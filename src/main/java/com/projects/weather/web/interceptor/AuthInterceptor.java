package com.projects.weather.web.interceptor;

import com.projects.weather.service.AuthService;
import com.projects.weather.util.SessionCookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthService authService;
    private final SessionCookieUtils sessionCookieUtils;

    @Autowired
    public AuthInterceptor(@Lazy AuthService authService, SessionCookieUtils sessionCookieUtils) {
        this.authService = authService;
        this.sessionCookieUtils = sessionCookieUtils;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        sessionCookieUtils.getSessionCookie(request).ifPresent(sessionCookie -> {
            var sessionId = UUID.fromString(sessionCookie.getValue());
            authService.findAuthorizedUserBySessionId(sessionId).ifPresent(authorizedUserDto ->
                    request.setAttribute(sessionCookieUtils.getAuthorizedUserAttribute(), authorizedUserDto));
        });

        return true;
    }
}
