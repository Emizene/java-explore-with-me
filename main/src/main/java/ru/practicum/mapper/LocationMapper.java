package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.model.Location;
import ru.practicum.LocationDto;
import ru.practicum.NewLocationDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LocationMapper {

    LocationDto toDto(Location location);

    Location toEntity(LocationDto locationDto);

    @Mapping(target = "id", ignore = true)
    Location toEntity(NewLocationDto newLocationDto);
}
