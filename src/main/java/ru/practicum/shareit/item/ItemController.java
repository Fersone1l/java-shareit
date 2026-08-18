package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @Validated @RequestBody ItemDto dto) {
        Item item = ItemMapper.dtoToItem(dto, userId);
        return ItemMapper.itemToDto(itemService.create(item));
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable Long id,
                          @RequestBody ItemDto dto) {
        Item item = ItemMapper.dtoToItem(dto, userId);
        return ItemMapper.itemToDto(itemService.update(item, id));
    }

    @GetMapping
    public List<ItemDto> getItemsByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ItemMapper.itemListToDtoList(itemService.getItemsByUser(userId));
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable Long id) {
        return ItemMapper.itemToDto(itemService.getItemById(id));
    }

    @GetMapping("/search")
    public List<ItemDto> getItemById(@RequestParam String text) {
        return ItemMapper.itemListToDtoList(itemService.getItemsByContent(text));
    }
}
