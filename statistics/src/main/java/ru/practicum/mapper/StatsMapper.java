package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.EndpointHit;
import ru.practicum.model.Hit;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StatsMapper {

    @Mapping(target = "id", ignore = true)
    Hit toEntity(EndpointHit endpointHit);
}