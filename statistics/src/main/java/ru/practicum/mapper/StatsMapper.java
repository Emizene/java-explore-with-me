package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.EndpointHit;
import ru.practicum.model.Hit;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StatsMapper {

    Hit toEntity(EndpointHit endpointHit);

    EndpointHit toDto(Hit hit);
}