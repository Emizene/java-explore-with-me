package ru.practicum;

import java.util.ArrayList;
import java.util.List;

public record EventRequestStatusUpdateResult(
        List<ParticipationRequestDto> confirmedRequests,
        List<ParticipationRequestDto> rejectedRequests
) {
    public EventRequestStatusUpdateResult() {
        this(new ArrayList<>(), new ArrayList<>());
    }
}