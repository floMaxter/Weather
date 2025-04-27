package com.projects.weather.security;

public interface PasswordEncoder {

    String encode(String rawPassword);
    boolean matches(String rawPassword, String encryptedPassword);
}
