package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> findAll();

    UserDto create(UserDto dto);

    void deleteUser(Long id);

    UserDto getUserById(Long id);

    UserDto update(Long id, UserDto dto);
}