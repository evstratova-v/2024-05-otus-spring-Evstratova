package ru.otus.hw.services;

import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.ShortBookDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookService {
    Optional<BookDto> findById(long id);

    Optional<ShortBookDto> findShortBookById(long id);

    List<BookDto> findAll();

    BookDto insert(String title, long authorId, Set<Long> genresIds);

    BookDto insert(ShortBookDto shortBookDto);

    BookDto update(long id, String title, long authorId, Set<Long> genresIds);

    BookDto update(ShortBookDto shortBookDto);

    void deleteById(long id);
}
