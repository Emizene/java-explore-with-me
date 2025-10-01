package ru.practicum.service.event;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.dto.AdminUpdateEventRequest;
import ru.practicum.dto.UpdateEventRequest;
import ru.practicum.emuns.EventSort;
import ru.practicum.eventDto.EventFullDto;
import ru.practicum.eventDto.EventShortDto;
import ru.practicum.eventDto.NewEventDto;
import ru.practicum.model.Event;

import java.util.List;

public interface EventService {
    List<EventShortDto> getUserEvents(Long userId, int from, int size);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getUserEvent(Long userId, Long eventId);

    EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventRequest updateEventRequest);

    List<EventFullDto> searchEvents(List<Long> users, List<String> states, List<Long> categories,
                                    String rangeStart, String rangeEnd, int from, int size);

    EventFullDto updateEventByAdmin(Long eventId, AdminUpdateEventRequest updateEventRequest);

    List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, String rangeStart,
                                  String rangeEnd, Boolean onlyAvailable, EventSort sort, int from, int size,
                                  HttpServletRequest request);

    EventFullDto getEventById(Long id, HttpServletRequest request);

    Event getEventEntityById(Long eventId);
}
