package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStorage;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.CommentStorage;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingStorage bookingStorage;
    private final CommentStorage commentStorage;

    @Override
    @Transactional
    public ItemDto create(ItemDto dto, Long userId) {
        User owner = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с таким id не существует"));

        Item item = ItemMapper.dtoToItem(dto, owner);
        return ItemMapper.itemToDto(itemStorage.save(item));
    }

    @Override
    @Transactional
    public ItemDto update(Long userId, ItemDto dto, Long id) {
        Item item = itemStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Такого объекта не существует"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Указан неверный id пользователя");
        }

        if (dto.getName() != null && !dto.getName().isBlank()) {
            item.setName(dto.getName());
        }
        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            item.setDescription(dto.getDescription());
        }
        if (dto.getAvailable() != null) {
            item.setAvailable(dto.getAvailable());
        }

        return ItemMapper.itemToDto(item);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemWithBookingsDto getItemById(Long id, Long userId) {
        Item item = itemStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Такого объекта не существует"));

        List<CommentDto> comments = CommentMapper.toDtoList(
                commentStorage.findAllByItemId(id));

        BookingShortDto lastDto = null;
        BookingShortDto nextDto = null;

        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();
            List<Booking> bookings = bookingStorage.findAllApprovedForItems(List.of(id));

            Booking last = null;
            Booking next = null;
            for (Booking b : bookings) {
                if (b.getStart().isBefore(now) && b.getEnd().isBefore(now)) {
                    if (last == null || b.getStart().isAfter(last.getStart())) {
                        last = b;
                    }
                } else if (b.getStart().isAfter(now)) {
                    if (next == null || b.getStart().isBefore(next.getStart())) {
                        next = b;
                    }
                }
            }

            lastDto = last != null ? BookingMapper.toShortDto(last) : null;
            nextDto = next != null ? BookingMapper.toShortDto(next) : null;
        }

        return ItemMapper.toDtoWithBookings(item, lastDto, nextDto, comments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemWithBookingsDto> getItemsByUser(Long userId) {
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("Пользователя с таким id не существует");
        }

        List<Item> items = itemStorage.findByOwnerId(userId);
        if (items.isEmpty()) {
            return List.of();
        }

        List<Long> itemIds = items.stream().map(Item::getId).toList();

        List<Booking> allBookings = bookingStorage.findAllApprovedForItems(itemIds);
        Map<Long, List<Booking>> bookingsByItem = allBookings.stream()
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));

        List<Comment> allComments = commentStorage.findAllByItemIds(itemIds);
        Map<Long, List<CommentDto>> commentsByItem = allComments.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getItem().getId(),
                        Collectors.mapping(CommentMapper::toDto, Collectors.toList())
                ));

        LocalDateTime now = LocalDateTime.now();
        List<ItemWithBookingsDto> result = new ArrayList<>();

        for (Item item : items) {
            List<Booking> bookings = bookingsByItem.getOrDefault(item.getId(), List.of());

            Booking last = null;
            Booking next = null;
            for (Booking b : bookings) {
                if (b.getStart().isBefore(now) && b.getEnd().isBefore(now)) {
                    if (last == null || b.getStart().isAfter(last.getStart())) {
                        last = b;
                    }
                } else if (b.getStart().isAfter(now)) {
                    if (next == null || b.getStart().isBefore(next.getStart())) {
                        next = b;
                    }
                }
            }

            BookingShortDto lastDto = last != null ? BookingMapper.toShortDto(last) : null;
            BookingShortDto nextDto = next != null ? BookingMapper.toShortDto(next) : null;
            List<CommentDto> comments = commentsByItem.getOrDefault(item.getId(), List.of());

            result.add(ItemMapper.toDtoWithBookings(item, lastDto, nextDto, comments));
        }
        return result;
    }

    @Override
    public List<ItemDto> getItemsByContent(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return ItemMapper.itemListToDtoList(itemStorage.search(text));
    }

    @Override
    @Transactional
    public CommentDto addComment(Long itemId, Long userId, CommentRequestDto dto) {
        User author = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с таким id не существует"));

        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещи с таким id не существует"));

        LocalDateTime now = LocalDateTime.now();
        List<Booking> completed = bookingStorage.findCompletedBookings(itemId, userId, now);
        if (completed.isEmpty()) {
            throw new ValidationException("Оставить отзыв можно только после завершённой аренды этой вещи");
        }

        Comment comment = new Comment();
        comment.setText(dto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(now);

        return CommentMapper.toDto(commentStorage.save(comment));
    }
}