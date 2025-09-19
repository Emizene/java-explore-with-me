package ru.practicum;

import java.util.List;

public record EventRequestStatusUpdateRequest(
        List<Long> requestIds,
        String status) {
}