package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.BookDto;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class BookConverter {
    public String bookToString(BookDto bookDto) {
        var genresString = bookDto.getGenres().stream()
                .map(genreDto -> "{Id: %d, Name: %s}".formatted(genreDto.getId(), genreDto.getName()))
                .collect(Collectors.joining(", "));
        return "Id: %d, title: %s, author: {Id: %d, FullName: %s}, genres: [%s]".formatted(
                bookDto.getId(),
                bookDto.getTitle(),
                bookDto.getAuthor().getId(),
                bookDto.getAuthor().getFullName(),
                genresString);
    }
}
