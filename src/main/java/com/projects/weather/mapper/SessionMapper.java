package com.projects.weather.mapper;

import com.projects.weather.dto.SessionDto;
import com.projects.weather.model.Session;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SessionMapper extends Mappable<SessionDto, Session> {

    @Override
    @Mapping(source = "userId", target = "user.id")
    Session mapFrom(SessionDto object);

    @Override
    @Mapping(source = "user.id", target = "userId")
    SessionDto mapTo(Session object);
}
