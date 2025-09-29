package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.model.Location;
import ru.practicum.locationDto.LocationDto;
import ru.practicum.locationDto.NewLocationDto;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LocationMapper {

    default LocationDto toDto(Location location) {
        return LocationDto.builder()
                .lat(round(location.getLat()))
                .lon(round(location.getLon()))
                .build();
    }

    Location toEntity(LocationDto locationDto);

    @Mapping(target = "id", ignore = true)
    Location toEntity(NewLocationDto newLocationDto);

    private Double round(Float value) {
        return BigDecimal.valueOf(value).setScale(4, RoundingMode.HALF_UP).doubleValue();
    }
}
