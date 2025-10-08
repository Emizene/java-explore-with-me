package ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewCommentDto {
    @NotBlank
    String text;
}
