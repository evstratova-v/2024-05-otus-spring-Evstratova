package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.otus.hw.models.Book;

import java.util.List;

@Data
@AllArgsConstructor
public class BookDto {

    private long id;

    private String title;

    private AuthorDto author;

    private List<GenreDto> genres;

    public static BookDto toDto(Book book) {
        List<GenreDto> genres = book.getGenres().stream().map(GenreDto::toDto).toList();
        return new BookDto(book.getId(), book.getTitle(), AuthorDto.toDto(book.getAuthor()), genres);
    }
}
