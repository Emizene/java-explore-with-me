package ru.practicum;

import java.time.LocalDateTime;

public record UpdateEventAdminRequest(
        String annotation,
        Long category,
        String description,
        LocalDateTime eventDate,
        LocationDto location,
        Boolean paid,
        Integer participantLimit,
        Boolean requestModeration,
        String stateAction,
        String title
) {
}
