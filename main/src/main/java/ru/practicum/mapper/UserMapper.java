package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.NewUserRequest;
import ru.practicum.model.User;
import ru.practicum.UserDto;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserDto toDto(User user);

    List<UserDto> toDtoList(List<User> users);

    @Mapping(target = "id", ignore = true)
    User toEntity(NewUserRequest newUserRequest);

    @Mapping(target = "id", ignore = true)
    User toEntity(UserDto userDto);
}
