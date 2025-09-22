package ru.practicum.eventDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.practicum.locationDto.LocationDto;

import java.time.LocalDateTime;

public record NewEventDto(
        @NotNull
        String annotation,
        @NotNull
        Long category,
        @NotBlank
        @Size(min = 20, max = 7000)
        String description,
        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime eventDate,
        @NotNull
        LocationDto location,
        Boolean paid,
        @Min(0)
        Integer participantLimit,
        Boolean requestModeration,
        @NotBlank
        @Size(min = 3, max = 120)
        String title
) {
}
