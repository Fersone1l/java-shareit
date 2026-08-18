package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    public Item save(Item item);

    public Item update(Item newItem, Long id);

    public Optional<Item> findById(Long id);

    public List<Item> findByOwnerId(Long ownerId);

    public List<Item> getItemsByContent(String text);
}
