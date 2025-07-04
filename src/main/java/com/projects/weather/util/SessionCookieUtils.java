package com.projects.weather.util;

import com.projects.weather.web.config.SessionProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Component
public class SessionCookieUtils {

    private final SessionProperties sessionProperties;

    @Autowired
    public SessionCookieUtils(SessionProperties sessionProperties) {
        this.sessionProperties = sessionProperties;
    }

    public void setSessionCookie(HttpServletResponse resp, UUID sessionId) {
        var sessionCookie = new Cookie(sessionProperties.getCookieName(), sessionId.toString());
        sessionCookie.setPath(sessionProperties.getCookiePath());
        sessionCookie.setHttpOnly(true);
        sessionCookie.setMaxAge(sessionProperties.getDurationSeconds());
        resp.addCookie(sessionCookie);
    }

    public Optional<Cookie> getSessionCookie(HttpServletRequest req) {
        if (req.getCookies() == null) return Optional.empty();

        return Arrays.stream(req.getCookies())
                .filter(cookie -> cookie.getName().equals(sessionProperties.getCookieName()))
                .findAny();
    }

    public void expireSessionCookie(HttpServletResponse resp) {
        var expiredCookie = new Cookie(sessionProperties.getCookieName(), null);
        expiredCookie.setPath(sessionProperties.getCookiePath());
        expiredCookie.setMaxAge(0);
        resp.addCookie(expiredCookie);
    }

    public String getAuthorizedUserAttribute() {
        return sessionProperties.getAuthorizedUserAttribute();
    }

    public boolean isValidSessionId(String uuid) {
        return Pattern.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$", uuid);
    }
}
