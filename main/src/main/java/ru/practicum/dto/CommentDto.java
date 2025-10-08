package ru.practicum.dto;

import lombok.Data;
import ru.practicum.userDto.UserShortDto;

@Data
public class CommentDto {
    Long id;
    String text;
    String created;
    UserShortDto author;
    EventDto event;
}
