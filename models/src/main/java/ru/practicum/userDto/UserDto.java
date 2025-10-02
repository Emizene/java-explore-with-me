package ru.practicum.userDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserDto(
        @Email
        @NotBlank
        @Size(min = 6, max = 254)
        String email,
        Long id,
        @NotBlank
        @Size(min = 2, max = 250)
        String name
) {
}
