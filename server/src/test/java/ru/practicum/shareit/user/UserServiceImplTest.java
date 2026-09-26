package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Test
    void create_shouldSaveUser() {
        UserDto dto = new UserDto();
        dto.setName("Ivan");
        dto.setEmail("ivan@mail.com");

        UserDto saved = userService.create(dto);

        assertNotNull(saved.getId());
        assertEquals("Ivan", saved.getName());
        assertEquals("ivan@mail.com", saved.getEmail());
    }

    @Test
    void create_whenEmailExists_shouldThrowConflict() {
        UserDto first = new UserDto();
        first.setName("A");
        first.setEmail("same@mail.com");
        userService.create(first);

        UserDto second = new UserDto();
        second.setName("B");
        second.setEmail("same@mail.com");

        assertThrows(ConflictException.class, () -> userService.create(second));
    }

    @Test
    void getById_whenNotFound_shouldThrow() {
        assertThrows(NotFoundException.class, () -> userService.getUserById(999L));
    }

    @Test
    void update_shouldChangeNameAndEmail() {
        UserDto created = userService.create(user("Old", "old@mail.com"));

        UserDto patch = new UserDto();
        patch.setName("New");
        patch.setEmail("new@mail.com");

        UserDto updated = userService.update(created.getId(), patch);

        assertEquals("New", updated.getName());
        assertEquals("new@mail.com", updated.getEmail());
    }

    private UserDto user(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }
}