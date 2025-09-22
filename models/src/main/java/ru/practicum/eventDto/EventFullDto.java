package ru.practicum.eventDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import ru.practicum.locationDto.LocationDto;
import ru.practicum.userDto.UserShortDto;
import ru.practicum.categoryDto.CategoryDto;

import java.time.LocalDateTime;

public record EventFullDto(
        @NotNull
        String annotation,
        @NotNull
        CategoryDto category,
        Integer confirmedRequests,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdOn,
        String description,
        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime eventDate,
        Long id,
        @NotNull
        UserShortDto initiator,
        @NotNull
        LocationDto location,
        @NotNull
        Boolean paid,
        Integer participantLimit,
        LocalDateTime publishedOn,
        Boolean requestModeration,
        String state,
        @NotNull
        String title,
        Long views
) {
}
