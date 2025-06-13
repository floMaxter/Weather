package com.projects.weather.web.resolver;

import com.projects.weather.dto.user.response.AuthorizedUserDto;
import com.projects.weather.util.SessionCookieUtils;
import com.projects.weather.web.annotation.AuthorizedUser;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class AuthorizedUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final SessionCookieUtils sessionCookieUtils;

    @Autowired
    public AuthorizedUserArgumentResolver(SessionCookieUtils sessionCookieUtils) {
        this.sessionCookieUtils = sessionCookieUtils;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthorizedUser.class)
               && AuthorizedUserDto.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public @Nullable Object resolveArgument(MethodParameter parameter,
                                            @Nullable ModelAndViewContainer mavContainer,
                                            NativeWebRequest webRequest,
                                            @Nullable WebDataBinderFactory binderFactory) {
        var request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request != null) {
            var userAttr = request.getAttribute(sessionCookieUtils.getAuthorizedUserAttribute());
            if (userAttr instanceof AuthorizedUserDto) {
                return userAttr;
            }
        }
        return null;
    }
}
