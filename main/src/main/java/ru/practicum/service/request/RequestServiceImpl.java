package ru.practicum.service.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.*;
import ru.practicum.emuns.EventState;
import ru.practicum.emuns.RequestStatus;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;

    @Override
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        checkUserExists(userId);
        checkEventExists(eventId);
        checkUserIsInitiator(userId, eventId);

        List<Request> requests = requestRepository.findAllByEventId(eventId);
        return requestMapper.toDtoList(requests);
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId,
                                                              EventRequestStatusUpdateRequest request) {
        checkUserExists(userId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found with id: " + eventId));
        checkUserIsInitiator(userId, eventId);

        List<Request> requests = requestRepository.findAllByIdIn(request.requestIds());

        if (requests.stream().anyMatch(r -> r.getStatus() != RequestStatus.PENDING)) {
            throw new ConflictException("All requests must be in PENDING status");
        }

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();

        switch (request.status()) {
            case "CONFIRMED":
                confirmRequests(requests, event, result);
                break;
            case "REJECTED":
                rejectRequests(requests, result);
                break;
            default:
                throw new ValidationException("Invalid status: " + request.status());
        }

        return result;
    }

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        checkUserExists(userId);
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        return requestMapper.toDtoList(requests);
    }

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found with id: " + eventId));

        validateRequest(userId, event);

        Request request = requestMapper.toEntity(userId, eventId);

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }

        Request savedRequest = requestRepository.save(request);
        return requestMapper.toDto(savedRequest);
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        checkUserExists(userId);
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException("Request not found with id: " + requestId + " for user: " + userId));

        request.setStatus(RequestStatus.CANCELED);
        Request updatedRequest = requestRepository.save(request);
        return requestMapper.toDto(updatedRequest);
    }

    @Override
    public Integer getConfirmedRequestsCount(Long eventId) {
        return requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
    }

    private void confirmRequests(List<Request> requests, Event event, EventRequestStatusUpdateResult result) {
        int confirmedCount = getConfirmedRequestsCount(event.getId());
        int availableSlots = event.getParticipantLimit() - confirmedCount;

        if (availableSlots <= 0) {
            throw new ConflictException("Event participant limit reached");
        }

        for (int i = 0; i < requests.size(); i++) {
            Request request = requests.get(i);
            if (i < availableSlots) {
                request.setStatus(RequestStatus.CONFIRMED);
                result.confirmedRequests().add(requestMapper.toDto(request));
            } else {
                request.setStatus(RequestStatus.REJECTED);
                result.rejectedRequests().add(requestMapper.toDto(request));
            }
        }

        requestRepository.saveAll(requests);
    }

    private void rejectRequests(List<Request> requests, EventRequestStatusUpdateResult result) {
        for (Request request : requests) {
            request.setStatus(RequestStatus.REJECTED);
            result.rejectedRequests().add(requestMapper.toDto(request));
        }
        requestRepository.saveAll(requests);
    }

    private void validateRequest(Long userId, Event event) {
        if (requestRepository.existsByRequesterIdAndEventId(userId, event.getId())) {
            throw new ConflictException("Request already exists for this event");
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Initiator cannot request participation in their own event");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Cannot participate in unpublished event");
        }

        int confirmedRequests = getConfirmedRequestsCount(event.getId());
        if (event.getParticipantLimit() > 0 && confirmedRequests >= event.getParticipantLimit()) {
            throw new ConflictException("Event participant limit reached");
        }
    }

    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found with id: " + userId);
        }
    }

    private void checkEventExists(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Event not found with id: " + eventId);
        }
    }

    private void checkUserIsInitiator(Long userId, Long eventId) {
        if (!eventRepository.existsByIdAndInitiatorId(eventId, userId)) {
            throw new ConflictException("User is not the initiator of this event");
        }
    }
}
