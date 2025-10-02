package ru.practicum.error;

import lombok.Builder;

import java.util.List;

@Builder
public record ApiError(String description, List<String> errors, String message, String reason, String status,
                       String timestamp) {
}