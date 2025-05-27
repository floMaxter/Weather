package com.projects.weather.web.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class SessionProperties {

    @Value("${session.cookie_name}")
    private String cookieName;

    @Value("${session.duration}")
    private int duration;

    @Value("${session.cookie_path}")
    private String cookiePath;

    @Value("${session.authorized_user_attribute}")
    private String authorizedUserAttribute;
}
