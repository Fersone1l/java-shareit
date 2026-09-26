package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserStorage;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService{

    private final ItemRequestStorage itemRequestStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;


    @Override
    @Transactional
    public ItemRequestDto create(ItemRequestCreateDto dto, Long userId) {
        User requestor = getUserOrThrow(userId);

        ItemRequest request = ItemRequestMapper.toEntity(dto, requestor);
        return ItemRequestMapper.toDto(itemRequestStorage.save(request));
    }

    @Override
    public List<ItemRequestDto> getOwnRequests(Long userId) {
        getUserOrThrow(userId);

        List<ItemRequest> requests = itemRequestStorage.findAllByRequestorIdOrderByCreatedDesc(userId);
        return mapRequestsWithItems(requests);
    }

    private List<ItemRequestDto> mapRequestsWithItems(List<ItemRequest> requests) {
        if (requests.isEmpty()) {
            return List.of();
        }

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .toList();

        List<Item> items = itemStorage.findAllByRequestIdIn(requestIds);
        Map<Long, List<ItemDto>> itemsByRequestId = items.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getRequest().getId(),
                        Collectors.mapping(ItemMapper::itemToDto, Collectors.toList())
                ));

        return requests.stream()
                .map(request -> ItemRequestMapper.toDto(
                        request,
                        itemsByRequestId.getOrDefault(request.getId(), Collections.emptyList())
                ))
                .toList();
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId) {
        getUserOrThrow(userId);

        List<ItemRequest> requests = itemRequestStorage.findAllByRequestorIdNotOrderByCreatedDesc(userId);
        return mapRequestsWithItems(requests);
    }

    @Override
    public ItemRequestDto getById(Long requestId, Long userId) {
        getUserOrThrow(userId);

        ItemRequest request = itemRequestStorage.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запроса с таким Id не существует"));

        List<Item> items = itemStorage.findAllByRequestIdIn(List.of(requestId));
        List<ItemDto> itemsDto = ItemMapper.itemListToDtoList(items);
        return ItemRequestMapper.toDto(request, itemsDto);
    }

    private User getUserOrThrow(Long userId) {
        return userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }
}
