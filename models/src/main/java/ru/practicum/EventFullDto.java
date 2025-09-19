package ru.practicum;

import java.time.LocalDateTime;

public record EventFullDto(
        String annotation,
        CategoryDto category,
        Integer confirmedRequests,
        LocalDateTime createdOn,
        String description,
        LocalDateTime eventDate,
        Long id,
        UserShortDto initiator,
        LocationDto location,
        Boolean paid,
        Integer participantLimit,
        LocalDateTime publishedOn,
        Boolean requestModeration,
        String state,
        String title,
        Long views
) {
}
