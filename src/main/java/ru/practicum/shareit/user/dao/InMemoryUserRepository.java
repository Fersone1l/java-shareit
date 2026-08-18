package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.*;

@Repository
public class InMemoryUserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long idCounter = 1L;

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idCounter++);
        }
        users.put(user.getId(), user);
        return user;
    }

    public User update(Long id, User newUser) {
        User user = users.get(id);
        if (newUser.getName() != null && !newUser.getName().isBlank()) {
            user.setName(newUser.getName());
        }
        if (newUser.getEmail() != null && !newUser.getEmail().isBlank()) {
            user.setEmail(newUser.getEmail());
        }
        return user;
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public void deleteById(Long id) {
        users.remove(id);
    }

    public boolean existsByEmail(String email) {
        return users.values().stream().anyMatch(u -> u.getEmail().equals(email));
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }
}
