package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ItemDto {
    private Long id;

    @NotBlank(message = "Имя должно быть указано")
    private String name;

    @NotBlank(message = "Описание должно быть указано")
    private String description;

    @NotNull(message = "Доступность должна быть указана")
    private Boolean available;

    private Long ownerId;
    private Long requestId;

    private List<CommentDto> comments;
}