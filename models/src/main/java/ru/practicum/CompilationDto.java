package ru.practicum;

import java.util.List;

public record CompilationDto(
        List<EventShortDto> events,
        Long id,
        Boolean pinned,
        String title
) {
}
