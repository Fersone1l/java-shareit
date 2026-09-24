package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.List;


public interface ItemService {

    public ItemDto create(ItemDto dto, Long userId);

    public ItemDto update(Long userId, ItemDto dto, Long id);

    ItemWithBookingsDto getItemById(Long id, Long userId);

    List<ItemWithBookingsDto> getItemsByUser(Long id);

    public List<ItemDto> getItemsByContent(String text);

    CommentDto addComment(Long itemId, Long userId, CommentRequestDto dto);
}
