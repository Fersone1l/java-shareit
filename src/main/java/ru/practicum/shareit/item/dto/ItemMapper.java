package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ItemMapper {

    public ItemDto itemToDto(Item item) {
        return itemToDto(item, List.of());
    }

    public ItemDto itemToDto(Item item, List<CommentDto> comments) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwnerId(item.getOwner().getId());
        if (item.getRequest() != null) {
            dto.setRequestId(item.getRequest().getId());
        }
        dto.setComments(comments);
        return dto;
    }

    public Item dtoToItem(ItemDto dto, User owner) {
        Item item = new Item();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        item.setOwner(owner);
        return item;
    }

    public List<ItemDto> itemListToDtoList(List<Item> items) {
        ArrayList<ItemDto> dtoItems = new ArrayList<>();
        for (Item item : items) {
            dtoItems.add(itemToDto(item));
        }
        return dtoItems;
    }

    public ItemWithBookingsDto toDtoWithBookings(Item item,
                                                 BookingShortDto last,
                                                 BookingShortDto next,
                                                 List<CommentDto> comments) {
        ItemWithBookingsDto dto = new ItemWithBookingsDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setLastBooking(last);
        dto.setNextBooking(next);
        dto.setComments(comments);
        return dto;
    }
}