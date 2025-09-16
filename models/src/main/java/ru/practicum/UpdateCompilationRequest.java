package ru.practicum;

import java.util.List;

public record UpdateCompilationRequest(
        List<Long> events,
        Boolean pinned,
        String title
) {
}
