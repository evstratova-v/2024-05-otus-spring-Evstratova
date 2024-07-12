package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.CommentDto;

@RequiredArgsConstructor
@Component
public class CommentConverter {
    public String commentToString(CommentDto commentDto) {
        return "Id: %d, text: %s, book: {Id: %d, title: %s}".formatted(commentDto.getId(), commentDto.getText(),
                commentDto.getBookId(), commentDto.getBookTitle());
    }
}
