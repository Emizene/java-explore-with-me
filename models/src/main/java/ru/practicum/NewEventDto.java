package ru.practicum;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record NewEventDto(
        String annotation,
        Long category,
        String description,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime eventDate,
        LocationDto location,
        Boolean paid,
        Integer participantLimit,
        Boolean requestModeration,
        String title
) {
}
