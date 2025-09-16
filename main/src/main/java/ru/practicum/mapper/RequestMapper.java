package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.Event;
import ru.practicum.ParticipationRequestDto;
import ru.practicum.Request;
import ru.practicum.User;
import ru.practicum.emuns.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, EventMapper.class})
public interface RequestMapper {

    @Mapping(source = "event.id", target = "event")
    @Mapping(source = "requester.id", target = "requester")
    ParticipationRequestDto toDto(Request request);

    List<ParticipationRequestDto> toDtoList(List<Request> requests);

    default Request toEntity(Long requesterId, Long eventId) {
        if (requesterId == null || eventId == null) {
            return null;
        }

        Request request = new Request();

        User requester = new User();
        requester.setId(requesterId);
        request.setRequester(requester);

        Event event = new Event();
        event.setId(eventId);
        request.setEvent(event);

        request.setCreated(LocalDateTime.now());
        request.setStatus(RequestStatus.PENDING);

        return request;
    }
}
