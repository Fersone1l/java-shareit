package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemRequestServiceImplTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    private Long requestorId;
    private Long otherUserId;

    @BeforeEach
    void setUp() {
        requestorId = userService.create(user("Requestor", "req@mail.com")).getId();
        otherUserId = userService.create(user("Other", "other@mail.com")).getId();
    }

    @Test
    void create_shouldSaveRequest() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Нужна дрель");

        ItemRequestDto saved = itemRequestService.create(dto, requestorId);

        assertNotNull(saved.getId());
        assertEquals("Нужна дрель", saved.getDescription());
        assertNotNull(saved.getCreated());
        assertTrue(saved.getItems() == null || saved.getItems().isEmpty());
    }

    @Test
    void getOwnRequests_shouldReturnUserRequests() {
        itemRequestService.create(request("Запрос 1"), requestorId);
        itemRequestService.create(request("Запрос 2"), requestorId);
        itemRequestService.create(request("Чужой"), otherUserId);

        List<ItemRequestDto> own = itemRequestService.getOwnRequests(requestorId);

        assertEquals(2, own.size());
        // от новых к старым
        assertEquals("Запрос 2", own.get(0).getDescription());
        assertEquals("Запрос 1", own.get(1).getDescription());
    }

    @Test
    void getAllRequests_shouldReturnOthersRequests() {
        itemRequestService.create(request("Мой"), requestorId);
        itemRequestService.create(request("Чужой"), otherUserId);

        List<ItemRequestDto> all = itemRequestService.getAllRequests(requestorId);

        assertEquals(1, all.size());
        assertEquals("Чужой", all.getFirst().getDescription());
    }

    @Test
    void getById_shouldReturnWithItems() {
        ItemRequestDto request = itemRequestService.create(request("Нужен молоток"), requestorId);

        ItemDto item = new ItemDto();
        item.setName("Молоток");
        item.setDescription("Тяжёлый");
        item.setAvailable(true);
        item.setRequestId(request.getId());
        itemService.create(item, otherUserId);

        ItemRequestDto found = itemRequestService.getById(request.getId(), requestorId);

        assertEquals(request.getId(), found.getId());
        assertEquals(1, found.getItems().size());
        assertEquals("Молоток", found.getItems().getFirst().getName());
    }

    @Test
    void getById_whenNotFound_shouldThrow() {
        assertThrows(NotFoundException.class,
                () -> itemRequestService.getById(999L, requestorId));
    }

    @Test
    void getOwnRequests_whenUserNotFound_shouldThrow() {
        assertThrows(NotFoundException.class,
                () -> itemRequestService.getOwnRequests(999L));
    }

    private UserDto user(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }

    private ItemRequestCreateDto request(String description) {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription(description);
        return dto;
    }
}