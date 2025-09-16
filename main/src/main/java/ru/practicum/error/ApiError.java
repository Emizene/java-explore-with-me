package ru.practicum.error;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ApiError {
    private final String description;
    private final List<String> errors;
    private final String message;
    private final String reason;
    private final String status;
    private final LocalDateTime timestamp;

    public ApiError(String description, List<String> errors, String message,
                    String reason, String status, LocalDateTime timestamp) {
        this.description = description;
        this.errors = errors;
        this.message = message;
        this.reason = reason;
        this.status = status;
        this.timestamp = timestamp;
    }
}
