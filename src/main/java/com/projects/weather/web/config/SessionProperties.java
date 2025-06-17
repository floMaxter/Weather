package com.projects.weather.web.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionProperties {

    @Value("${session.cookie_name}")
    private String cookieName;

    @Value("${session.duration_seconds}")
    private int durationSeconds;

    @Value("${session.cookie_path}")
    private String cookiePath;

    @Value("${session.authorized_user_attribute}")
    private String authorizedUserAttribute;
}
