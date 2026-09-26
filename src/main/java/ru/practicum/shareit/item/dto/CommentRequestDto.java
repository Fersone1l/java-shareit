package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentRequestDto {
    @NotBlank(message = "Текст не может быть пустым")
    @Size(max = 1000, message = "Количество символов ограничено до 10000")
    private String text;
}