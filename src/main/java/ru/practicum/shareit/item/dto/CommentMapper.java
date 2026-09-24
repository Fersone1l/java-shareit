package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.Comment;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class CommentMapper {

    public CommentDto toDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setAuthorName(comment.getAuthor().getName());
        dto.setCreated(comment.getCreated());
        return dto;
    }

    public List<CommentDto> toDtoList(List<Comment> comments) {
        List<CommentDto> result = new ArrayList<>();
        for (Comment c : comments) {
            result.add(toDto(c));
        }
        return result;
    }
}