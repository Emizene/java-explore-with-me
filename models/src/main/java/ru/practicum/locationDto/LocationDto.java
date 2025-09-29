package ru.practicum.locationDto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LocationDto {
    private Double lat;
    private Double lon;
}
