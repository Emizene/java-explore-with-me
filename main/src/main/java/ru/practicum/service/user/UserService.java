package ru.practicum.service.user;

import ru.practicum.NewUserRequest;
import ru.practicum.User;
import ru.practicum.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(List<Long> ids, int from, int size);

    UserDto createUser(NewUserRequest newUserRequest);

    void deleteUser(Long userId);

    User getUserById(Long userId);
}
