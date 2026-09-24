package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingRequestDto dto, Long userId);
    BookingDto approve(Long bookingId, Long ownerId, boolean approved);
    BookingDto getById(Long bookingId, Long userId);
    List<BookingDto> getUserBookings(Long userId, State state);
    List<BookingDto> getOwnerBookings(Long userId, State state);
}