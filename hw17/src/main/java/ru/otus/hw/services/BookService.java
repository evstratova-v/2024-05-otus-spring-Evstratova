package ru.otus.hw.services;

import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.ShortBookDto;

import java.util.List;
import java.util.Optional;

public interface BookService {
    Optional<ShortBookDto> findShortBookById(long id);

    List<BookDto> findAll();

    BookDto insert(ShortBookDto shortBookDto);

    BookDto update(ShortBookDto shortBookDto);

    void deleteById(long id);
}
