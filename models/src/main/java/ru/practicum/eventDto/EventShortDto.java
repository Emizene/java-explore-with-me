package ru.practicum.eventDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.practicum.userDto.UserShortDto;
import ru.practicum.categoryDto.CategoryDto;

import java.time.LocalDateTime;

public record EventShortDto(
        @NotNull
        String annotation,
        @NotNull
        CategoryDto category,
        Integer confirmedRequests,
        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime eventDate,
        Long id,
        @NotNull
        UserShortDto initiator,
        @NotNull
        Boolean paid,
        @NotNull
        String title,
        Long views
) {
    public EventShortDto withStats(Integer confirmedRequests, Long views) {
        return new EventShortDto(
                annotation,
                category,
                confirmedRequests,
                eventDate,
                id,
                initiator,
                paid,
                title,
                views
        );
    }
}
