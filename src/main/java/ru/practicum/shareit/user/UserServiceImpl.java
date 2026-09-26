package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public List<UserDto> findAll() {
        return UserMapper.userListToDtoList(userStorage.findAll());
    }

    @Override
    @Transactional
    public UserDto create(UserDto dto) {
        User user = UserMapper.dtoToUser(dto);
        if (userStorage.existsByEmail(user.getEmail())) {
            throw new ConflictException("Данная почта уже используется другим пользователем");
        }
        return UserMapper.userToDto(userStorage.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userStorage.existsById(id)) {
            throw new NotFoundException("Пользователя с таким id не существует");
        }
        userStorage.deleteById(id);
    }

    @Override
    public UserDto getUserById(Long id) {
        return UserMapper.userToDto(userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователя с таким id не существует")));
    }

    @Override
    @Transactional
    public UserDto update(Long id, UserDto dto) {
        User user = userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователя с таким id не существует"));

        if (dto.getName() != null && !dto.getName().isBlank()) {
            user.setName(dto.getName());
        }
        if (dto.getEmail() != null && !dto.getEmail().isBlank()
                && !user.getEmail().equals(dto.getEmail())) {
            if (userStorage.existsByEmail(dto.getEmail())) {
                throw new ConflictException("Данная почта уже используется другим пользователем");
            }
            user.setEmail(dto.getEmail());
        }

        return UserMapper.userToDto(user);
    }
}