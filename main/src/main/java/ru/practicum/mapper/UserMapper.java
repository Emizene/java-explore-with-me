package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.NewUserRequest;
import ru.practicum.User;
import ru.practicum.UserDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    List<UserDto> toDtoList(List<User> users);

    @Mapping(target = "id", ignore = true)
    User toEntity(NewUserRequest newUserRequest);

    @Mapping(target = "id", ignore = true)
    User toEntity(UserDto userDto);
}
