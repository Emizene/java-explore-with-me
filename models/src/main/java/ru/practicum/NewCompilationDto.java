package ru.practicum;

import java.util.Set;

public record NewCompilationDto(
        Set<Long> events,
        Boolean pinned,
        String title
) {
}
