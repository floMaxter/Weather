package com.projects.weather.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(
        @NotBlank(message = "The login should not be empty")
        @Size(min = 5, max = 255, message = "The login should be in the range of 5-255 characters")
        @Email(message = "The login should look like name@gmail.com")
        String login,
        @NotBlank(message = "The password should be empty")
        @Size(min = 5, max = 255, message = "The password should be in the range of 5-255 characters")
        String password) {
}
