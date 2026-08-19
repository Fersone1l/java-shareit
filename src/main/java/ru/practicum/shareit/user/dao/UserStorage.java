package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User save(User user);

    User update(Long id, User newUser);

    List<User> findAll();

    void deleteById(Long id);

    boolean existsByEmail(String email);

    Optional<User> findById(Long id);
}
