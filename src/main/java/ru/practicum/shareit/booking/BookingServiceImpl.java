package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingStorage bookingRepository;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    private static final Sort SORT_DESC_BY_START = Sort.by(Sort.Direction.DESC, "start");

    @Override
    @Transactional
    public BookingDto create(BookingRequestDto dto, Long userId) {
        if (!dto.getEnd().isAfter(dto.getStart())) {
            throw new ValidationException("Некорректные даты бронирования");
        }

        User booker = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с таким id не существует"));

        Item item = itemStorage.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещи с таким id не существует"));

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Владелец не может бронировать свою вещь");
        }

        Booking booking = new Booking();
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setItem(item);
        booking.setBookerId(booker.getId());
        booking.setStatus(Status.WAITING);

        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto approve(Long bookingId, Long ownerId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирования с таким id не существует"));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Подтвердить бронирование может только владелец вещи");
        }
        if (booking.getStatus() != Status.WAITING) {
            throw new ValidationException("Бронирование уже обработано");
        }

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return BookingMapper.toDto(booking);
    }

    @Override
    public BookingDto getById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирования с таким id не существует"));

        boolean isBooker = booking.getBookerId().equals(userId);
        boolean isOwner = booking.getItem().getOwner().getId().equals(userId);
        if (!isBooker && !isOwner) {
            throw new NotFoundException("Нет доступа к этому бронированию");
        }
        return BookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, State state) {
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("Пользователя с таким id не существует");
        }
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (state) {
            case ALL      -> bookingRepository.findAllByBookerId(userId, SORT_DESC_BY_START);
            case CURRENT  -> bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(
                    userId, now, now, SORT_DESC_BY_START);
            case PAST     -> bookingRepository.findAllByBookerIdAndEndBefore(
                    userId, now, SORT_DESC_BY_START);
            case FUTURE   -> bookingRepository.findAllByBookerIdAndStartAfter(
                    userId, now, SORT_DESC_BY_START);
            case WAITING  -> bookingRepository.findAllByBookerIdAndStatus(
                    userId, Status.WAITING, SORT_DESC_BY_START);
            case REJECTED -> bookingRepository.findAllByBookerIdAndStatus(
                    userId, Status.REJECTED, SORT_DESC_BY_START);
        };
        return BookingMapper.toDtoList(bookings);
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long userId, State state) {
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("Пользователя с таким id не существует");
        }
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (state) {
            case ALL      -> bookingRepository.findAllByOwnerId(userId, SORT_DESC_BY_START);
            case CURRENT  -> bookingRepository.findCurrentByOwnerId(userId, now, SORT_DESC_BY_START);
            case PAST     -> bookingRepository.findPastByOwnerId(userId, now, SORT_DESC_BY_START);
            case FUTURE   -> bookingRepository.findFutureByOwnerId(userId, now, SORT_DESC_BY_START);
            case WAITING  -> bookingRepository.findByOwnerIdAndStatus(
                    userId, Status.WAITING, SORT_DESC_BY_START);
            case REJECTED -> bookingRepository.findByOwnerIdAndStatus(
                    userId, Status.REJECTED, SORT_DESC_BY_START);
        };
        return BookingMapper.toDtoList(bookings);
    }
}