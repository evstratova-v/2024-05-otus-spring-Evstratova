package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.otus.hw.models.Comment;

@Data
@AllArgsConstructor
public class CommentDto {

    private long id;

    private String text;

    public static CommentDto toDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText());
    }
}
