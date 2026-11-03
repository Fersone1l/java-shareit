package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceImplTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    private Long ownerId;

    @BeforeEach
    void setUp() {
        UserDto owner = new UserDto();
        owner.setName("Owner");
        owner.setEmail("owner@mail.com");
        ownerId = userService.create(owner).getId();
    }

    @Test
    void create_shouldSaveItem() {
        ItemDto dto = item("Дрель", "Мощная", true);

        ItemDto saved = itemService.create(dto, ownerId);

        assertNotNull(saved.getId());
        assertEquals("Дрель", saved.getName());
        assertEquals(ownerId, saved.getOwnerId());
    }

    @Test
    void getItemsByUser_shouldReturnOwnerItems() {
        itemService.create(item("A", "desc A", true), ownerId);
        itemService.create(item("B", "desc B", true), ownerId);

        List<ItemWithBookingsDto> items = itemService.getItemsByUser(ownerId);

        assertEquals(2, items.size());
    }

    @Test
    void getItemsByUser_whenUserNotFound_shouldThrow() {
        assertThrows(NotFoundException.class, () -> itemService.getItemsByUser(999L));
    }

    @Test
    void search_shouldFindByName() {
        itemService.create(item("Дрель Bosch", "инструмент", true), ownerId);
        itemService.create(item("Молоток", "ручной", true), ownerId);

        List<ItemDto> found = itemService.getItemsByContent("дрель");

        assertEquals(1, found.size());
        assertTrue(found.getFirst().getName().toLowerCase().contains("дрель"));
    }

    @Test
    void search_whenBlank_shouldReturnEmpty() {
        assertTrue(itemService.getItemsByContent("  ").isEmpty());
    }

    private ItemDto item(String name, String description, boolean available) {
        ItemDto dto = new ItemDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);
        return dto;
    }
}