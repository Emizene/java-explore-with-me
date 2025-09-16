package ru.practicum.service.request;

import ru.practicum.ParticipationRequestDto;
import ru.practicum.EventRequestStatusUpdateRequest;
import ru.practicum.EventRequestStatusUpdateResult;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId,
                                                       EventRequestStatusUpdateRequest request);

    List<ParticipationRequestDto> getUserRequests(Long userId);

    ParticipationRequestDto createRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    Integer getConfirmedRequestsCount(Long eventId);
}
