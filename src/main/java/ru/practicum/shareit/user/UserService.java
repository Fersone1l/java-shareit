package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.InMemoryUserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final InMemoryUserRepository userStorage;

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        if (userStorage.existsByEmail(user.getEmail())) {
            throw new ConflictException("Данная почта уже используется другим пользователем");
        }
        return userStorage.save(user);
    }

    public void deleteUser(Long id) {
        userStorage.deleteById(id);
    }

    public User getUserById(Long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователя с таким id не существует"));
    }

    public User update(Long id, User newUser) {
        User user = getUserById(id);
        if (userStorage.existsByEmail(newUser.getEmail())
                && !user.getEmail().equals(newUser.getEmail())) {
            System.out.println("ConflictException!");
            throw new ConflictException("Данная почта уже используется другим пользователем");
        }
        return userStorage.update(id, newUser);
    }
}
