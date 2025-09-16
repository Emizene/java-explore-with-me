package ru.practicum;

import java.time.LocalDateTime;

public record ParticipationRequestDto(
        LocalDateTime created,
        Long event,
        Long id,
        Long requester,
        String status
) {
}
