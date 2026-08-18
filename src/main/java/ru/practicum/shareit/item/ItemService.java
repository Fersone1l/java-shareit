package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.InMemoryItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final InMemoryItemRepository itemStorage;
    private final UserService userService;

    public Item create(Item item) {
        userService.getUserById(item.getOwnerId());
        return itemStorage.save(item);
    }

    public Item update(Item newItem, Long id) {
        Item item = getItemById(id);
        userService.getUserById(newItem.getOwnerId());
        if (!item.getOwnerId().equals(newItem.getOwnerId())) {
            throw new NotFoundException("Указан неверный id пользователя");
        }
        return itemStorage.update(newItem, id);
    }

    public Item getItemById(Long id) {
        return itemStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Такого объекта не существует"));
    }

    public List<Item> getItemsByUser(Long id) {
        userService.getUserById(id);
        return itemStorage.findByOwnerId(id);
    }

    public List<Item> getItemsByContent(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }

        return itemStorage.getItemsByContent(text);
    }
}
