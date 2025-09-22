package ru.practicum.service.user;

import ru.practicum.requestDto.NewUserRequest;
import ru.practicum.userDto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(List<Long> ids, int from, int size);

    UserDto createUser(NewUserRequest newUserRequest);

    void deleteUser(Long userId);
}
