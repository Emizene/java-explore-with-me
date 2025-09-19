package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.*;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {UserMapper.class, CategoryMapper.class, LocationMapper.class})
public interface EventMapper {

    @Mapping(source = "eventDate", target = "eventDate")
    @Mapping(source = "createdOn", target = "createdOn")
    @Mapping(source = "publishedOn", target = "publishedOn")
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    EventFullDto toFullDto(Event event);

    @Mapping(source = "eventDate", target = "eventDate")
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    EventShortDto toShortDto(Event event);

    List<EventFullDto> toFullDtoList(List<Event> events);

    List<EventShortDto> toShortDtoList(List<Event> events);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "requests", ignore = true)
    @Mapping(target = "compilations", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "statistics", ignore = true)
    @Mapping(source = "categoryId", target = "category.id")
    Event toEntity(NewEventDto newEventDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "requests", ignore = true)
    @Mapping(target = "compilations", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "statistics", ignore = true)
    @Mapping(source = "categoryId", target = "category.id")
    Event toEntity(UpdateEventRequest updateEventRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "requests", ignore = true)
    @Mapping(target = "compilations", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "statistics", ignore = true)
    @Mapping(source = "categoryId", target = "category.id")
    Event toEntity(AdminUpdateEventRequest adminUpdateEventRequest);
}