package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public List<UserDto> findAll() {
        return UserMapper.userListToDtoList(userStorage.findAll());
    }

    public UserDto create(UserDto dto) {
        User user = UserMapper.dtoToUser(dto);
        if (userStorage.existsByEmail(user.getEmail())) {
            throw new ConflictException("Данная почта уже используется другим пользователем");
        }
        return UserMapper.userToDto(userStorage.save(user));
    }

    public void deleteUser(Long id) {
        userStorage.deleteById(id);
    }

    public UserDto getUserById(Long id) {
        return UserMapper.userToDto(userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователя с таким id не существует")));
    }

    public UserDto update(Long id, UserDto dto) {
        User newUser = UserMapper.dtoToUser(dto);
        User user = userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователя с таким id не существует"));
        if (userStorage.existsByEmail(newUser.getEmail())
                && !user.getEmail().equals(newUser.getEmail())) {
            throw new ConflictException("Данная почта уже используется другим пользователем");
        }
        return UserMapper.userToDto(userStorage.update(id, newUser));
    }
}
