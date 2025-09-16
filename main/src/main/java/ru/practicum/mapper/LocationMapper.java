package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.Location;
import ru.practicum.LocationDto;
import ru.practicum.NewLocationDto;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    LocationDto toDto(Location location);

    Location toEntity(LocationDto locationDto);

    @Mapping(target = "id", ignore = true)
    Location toEntity(NewLocationDto newLocationDto);
}
