package ru.practicum;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class NewLocationDto {
    @NotNull
    private Double lat;

    @NotNull
    private Double lon;
}
