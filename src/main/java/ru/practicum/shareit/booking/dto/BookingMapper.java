package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemShortDto;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class BookingMapper {

    public BookingDto toDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus().name());

        ItemShortDto itemDto = new ItemShortDto();
        itemDto.setId(booking.getItem().getId());
        itemDto.setName(booking.getItem().getName());
        dto.setItem(itemDto);

        BookerDto bookerDto = new BookerDto();
        bookerDto.setId(booking.getBooker().getId());
        bookerDto.setName(booking.getBooker().getName());
        dto.setBooker(bookerDto);

        return dto;
    }

    public List<BookingDto> toDtoList(List<Booking> bookings) {
        List<BookingDto> result = new ArrayList<>();
        for (Booking b : bookings) {
            result.add(toDto(b));
        }
        return result;
    }

    public BookingShortDto toShortDto(Booking booking) {
        BookingShortDto dto = new BookingShortDto();
        dto.setId(booking.getId());
        dto.setBookerId(booking.getBooker().getId());
        return dto;
    }
}