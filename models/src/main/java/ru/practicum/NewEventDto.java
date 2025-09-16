package ru.practicum;

import java.time.LocalDateTime;

public record NewEventDto(
        String annotation,
        Long categoryId,
        String description,
        LocalDateTime eventDate,
        LocationDto location,
        Boolean paid,
        Integer participantLimit,
        Boolean requestModeration,
        String title
) {
}
