package ru.practicum.eventDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.practicum.userDto.UserShortDto;
import ru.practicum.categoryDto.CategoryDto;

import java.time.LocalDateTime;

public record EventShortDto(
        @NotNull
        Long id,
        @NotNull
        String annotation,
        @NotNull
        CategoryDto category,
        Integer confirmedRequests,
        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime eventDate,
        @NotNull
        UserShortDto initiator,
        @NotNull
        Boolean paid,
        @NotNull
        String title,
        Integer views
) {
    public EventShortDto withStats(Integer confirmedRequests, Integer views) {
        return new EventShortDto(
                id,
                annotation,
                category,
                confirmedRequests,
                eventDate,
                initiator,
                paid,
                title,
                views
        );
    }
}
