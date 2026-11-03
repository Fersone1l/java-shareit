package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRequestStorage extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(Long requestorId);

    List<ItemRequest> findAllByRequestorIdNotOrderByCreatedDesc(Long requestorId);
}
