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
public class ConstraintProperties {

    @Value("${constraints.users.login}")
    private String usersLoginConstraint;
}
