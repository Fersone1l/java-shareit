package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    public User save(User user);

    public User update(Long id, User newUser);

    public List<User> findAll();

    public void deleteById(Long id);

    public boolean existsByEmail(String email);

    public Optional<User> findById(Long id);
}
