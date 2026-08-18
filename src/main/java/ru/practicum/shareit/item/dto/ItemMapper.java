package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemMapper {
    public static ItemDto itemToDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        return dto;
    }

    public static Item dtoToItem(ItemDto dto, Long userId) {
        Item item = new Item();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setOwnerId(userId);
        item.setAvailable(dto.getAvailable());
        return item;
    }

    public static List<ItemDto> itemListToDtoList(List<Item> items) {
        ArrayList<ItemDto> dtoItems = new ArrayList<>();
        for (Item item : items) {
            dtoItems.add(itemToDto(item));
        }

        return dtoItems;
    }
}
