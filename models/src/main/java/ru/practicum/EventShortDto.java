package ru.practicum;

import java.time.LocalDateTime;

public record EventShortDto(
        String annotation,
        CategoryDto category,
        Integer confirmedRequests,
        LocalDateTime eventDate,
        Long id,
        UserShortDto initiator,
        Boolean paid,
        String title,
        Long views
) {
}
