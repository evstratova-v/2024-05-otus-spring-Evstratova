package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.Map;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class BookDto {

    private long id;

    private String title;

    private long authorId;

    private String authorFullName;

    private Map<Long, String> genres;

    public static BookDto toDto(Book book) {
        Map<Long, String> genres = book.getGenres().stream().collect(Collectors.toMap(Genre::getId, Genre::getName));
        return new BookDto(book.getId(),
                book.getTitle(),
                book.getAuthor().getId(),
                book.getAuthor().getFullName(),
                genres);
    }
}
