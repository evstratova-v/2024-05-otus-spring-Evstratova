package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.CommentDto;

@RequiredArgsConstructor
@Component
public class CommentConverter {
    public String commentToString(CommentDto commentDto) {
        return "Id: %d, text: %s".formatted(commentDto.getId(), commentDto.getText());
    }
}
