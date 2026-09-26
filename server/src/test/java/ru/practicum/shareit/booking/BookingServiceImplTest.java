package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BookingServiceImplTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    private Long ownerId;
    private Long bookerId;
    private Long itemId;

    @BeforeEach
    void setUp() {
        ownerId = userService.create(user("Owner", "owner@mail.com")).getId();
        bookerId = userService.create(user("Booker", "booker@mail.com")).getId();

        ItemDto item = new ItemDto();
        item.setName("Дрель");
        item.setDescription("Мощная");
        item.setAvailable(true);
        itemId = itemService.create(item, ownerId).getId();
    }

    @Test
    void create_shouldCreateWaitingBooking() {
        BookingRequestDto dto = bookingDto(itemId,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));

        BookingDto saved = bookingService.create(dto, bookerId);

        assertNotNull(saved.getId());
        assertEquals("WAITING", saved.getStatus());
        assertEquals(itemId, saved.getItem().getId());
    }

    @Test
    void create_whenItemNotAvailable_shouldThrow() {
        ItemDto unavailable = new ItemDto();
        unavailable.setName("Сломанная");
        unavailable.setDescription("не работает");
        unavailable.setAvailable(false);
        Long badItemId = itemService.create(unavailable, ownerId).getId();

        BookingRequestDto dto = bookingDto(badItemId,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));

        assertThrows(ValidationException.class, () -> bookingService.create(dto, bookerId));
    }

    @Test
    void create_whenOwnerBooksOwnItem_shouldThrow() {
        BookingRequestDto dto = bookingDto(itemId,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));

        assertThrows(NotFoundException.class, () -> bookingService.create(dto, ownerId));
    }

    @Test
    void create_whenEndBeforeStart_shouldThrow() {
        BookingRequestDto dto = bookingDto(itemId,
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1));

        assertThrows(ValidationException.class, () -> bookingService.create(dto, bookerId));
    }

    @Test
    void approve_shouldApproveBooking() {
        BookingDto booking = bookingService.create(
                bookingDto(itemId, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)),
                bookerId);

        BookingDto approved = bookingService.approve(booking.getId(), ownerId, true);

        assertEquals("APPROVED", approved.getStatus());
    }

    @Test
    void approve_whenNotOwner_shouldThrow() {
        BookingDto booking = bookingService.create(
                bookingDto(itemId, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)),
                bookerId);

        assertThrows(ForbiddenException.class,
                () -> bookingService.approve(booking.getId(), bookerId, true));
    }

    @Test
    void getById_whenBooker_shouldReturn() {
        BookingDto booking = bookingService.create(
                bookingDto(itemId, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)),
                bookerId);

        BookingDto found = bookingService.getById(booking.getId(), bookerId);

        assertEquals(booking.getId(), found.getId());
    }

    @Test
    void getById_whenStranger_shouldThrow() {
        Long strangerId = userService.create(user("Stranger", "s@mail.com")).getId();
        BookingDto booking = bookingService.create(
                bookingDto(itemId, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)),
                bookerId);

        assertThrows(NotFoundException.class,
                () -> bookingService.getById(booking.getId(), strangerId));
    }

    @Test
    void getUserBookings_all_shouldReturnList() {
        bookingService.create(
                bookingDto(itemId, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)),
                bookerId);

        List<BookingDto> bookings = bookingService.getUserBookings(bookerId, State.ALL);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getOwnerBookings_all_shouldReturnList() {
        bookingService.create(
                bookingDto(itemId, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)),
                bookerId);

        List<BookingDto> bookings = bookingService.getOwnerBookings(ownerId, State.ALL);

        assertFalse(bookings.isEmpty());
    }

    private UserDto user(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }

    private BookingRequestDto bookingDto(Long itemId, LocalDateTime start, LocalDateTime end) {
        BookingRequestDto dto = new BookingRequestDto();
        dto.setItemId(itemId);
        dto.setStart(start);
        dto.setEnd(end);
        return dto;
    }

}