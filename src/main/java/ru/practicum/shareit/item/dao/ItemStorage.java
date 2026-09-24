package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Item save(Item item);

    Item update(Item newItem, Long id);

    Optional<Item> findById(Long id);

    List<Item> findByOwnerId(Long ownerId);

    List<Item> getItemsByContent(String text);
}
