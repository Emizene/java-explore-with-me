package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.dto.AdminUpdateEventRequest;
import ru.practicum.dto.UpdateEventRequest;
import ru.practicum.emuns.RequestStatus;
import ru.practicum.eventDto.EventFullDto;
import ru.practicum.eventDto.EventShortDto;
import ru.practicum.eventDto.NewEventDto;
import ru.practicum.model.Event;
import ru.practicum.model.Request;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring",
        uses = {UserMapper.class, CategoryMapper.class, LocationMapper.class})
public interface EventMapper {

    @Mapping(source = "eventDate", target = "eventDate")
    @Mapping(source = "createdOn", target = "createdOn")
    @Mapping(source = "publishedOn", target = "publishedOn")
    @Mapping(source = "views", target = "views", qualifiedByName = "mapViews")
    @Mapping(target = "confirmedRequests", source = "requests", qualifiedByName = "mapRequests")
    EventFullDto toFullDto(Event event);

    @Mapping(source = "eventDate", target = "eventDate")
    @Mapping(source = "views", target = "views", qualifiedByName = "mapViews")
    @Mapping(target = "confirmedRequests", source = "requests", qualifiedByName = "mapRequests")
    EventShortDto toShortDto(Event event);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "requests", ignore = true)
    @Mapping(target = "compilations", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(source = "category", target = "category.id")
    Event toEntity(NewEventDto newEventDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "requests", ignore = true)
    @Mapping(target = "compilations", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(source = "categoryId", target = "category.id")
    Event toEntity(UpdateEventRequest updateEventRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "requests", ignore = true)
    @Mapping(target = "compilations", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(source = "categoryId", target = "category.id")
    Event toEntity(AdminUpdateEventRequest adminUpdateEventRequest);

    @Named("mapViews")
    default Integer mapViews(List<String> views) {
        return views != null ? views.size() : 0;
    }

    @Named("mapRequests")
    default Integer mapRequests(Set<Request> requests) {
        return requests.stream()
                .filter(it -> it.getStatus() == RequestStatus.CONFIRMED)
                .toList()
                .size();
    }
}