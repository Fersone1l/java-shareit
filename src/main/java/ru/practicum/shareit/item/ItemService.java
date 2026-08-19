package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    public ItemDto create(ItemDto dto, Long userId) {
        Item item = ItemMapper.dtoToItem(dto, userId);
        userService.getUserById(item.getOwnerId());
        return ItemMapper.itemToDto(itemStorage.save(item));
    }

    public ItemDto update(Long userId, ItemDto dto, Long id) {
        Item newItem = ItemMapper.dtoToItem(dto, userId);
        Item item = itemStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Такого объекта не существует"));
        userService.getUserById(newItem.getOwnerId());
        if (!item.getOwnerId().equals(newItem.getOwnerId())) {
            throw new NotFoundException("Указан неверный id пользователя");
        }
        return ItemMapper.itemToDto(itemStorage.update(newItem, id));
    }

    public ItemDto getItemById(Long id) {
        return ItemMapper.itemToDto(itemStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Такого объекта не существует")));
    }

    public List<ItemDto> getItemsByUser(Long id) {
        userService.getUserById(id);
        return ItemMapper.itemListToDtoList(itemStorage.findByOwnerId(id));
    }

    public List<ItemDto> getItemsByContent(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }

        return ItemMapper.itemListToDtoList(itemStorage.getItemsByContent(text));
    }
}
