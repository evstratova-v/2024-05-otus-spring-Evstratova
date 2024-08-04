package ru.otus.hw.services;

import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookWithoutComments;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookService {
    Optional<Book> findById(String id);

    List<BookWithoutComments> findAll();

    Book insert(String title, String authorId, Set<String> genresIds);

    Book update(String id, String title, String authorId, Set<String> genresIds);

    Book update(String id, Comment comment);

    void deleteById(String id);
}
