package ru.practicum.compilationDto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.practicum.eventDto.EventShortDto;

import java.util.List;

public record CompilationDto(
        List<EventShortDto> events,
        @NotNull
        Long id,
        @NotNull
        Boolean pinned,
        @NotNull
        @Size(max = 50)
        String title
) {
}
