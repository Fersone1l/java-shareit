package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @Validated @RequestBody ItemDto dto) {
        return itemService.create(dto, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable Long id,
                          @RequestBody ItemDto dto) {
        return itemService.update(userId, dto, id);
    }

    @GetMapping
    public List<ItemWithBookingsDto> getItemsByUser(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemsByUser(userId);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingsDto getItemById(@PathVariable Long itemId,
                                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemByText(@RequestParam String text) {
        return itemService.getItemsByContent(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable Long itemId,
                                 @RequestHeader("X-Sharer-User-Id") Long userId,
                                 @Valid @RequestBody CommentRequestDto dto) {
        return itemService.addComment(itemId, userId, dto);
    }
}
