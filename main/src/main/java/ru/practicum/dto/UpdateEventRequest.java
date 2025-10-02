package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.locationDto.LocationDto;

import java.time.LocalDateTime;

@Data
public class UpdateEventRequest {
    @Size(min = 20, max = 2000)
    private String annotation;

    private Long categoryId;

    @Size(min = 20, max = 7000)
    private String description;

    @Future
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private LocationDto location;
    private Boolean paid;
    @Min(0)
    private Integer participantLimit;
    private Boolean requestModeration;

    @Size(min = 3, max = 120)
    private String title;

    private String stateAction;
}
