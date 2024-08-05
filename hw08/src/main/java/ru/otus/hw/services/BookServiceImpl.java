package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookWithoutComments;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;

    @Override
    public Optional<Book> findById(String id) {
        return bookRepository.findById(id);
    }

    @Override
    public List<BookWithoutComments> findAll() {
        return bookRepository.findAllWithoutCommentsBy();
    }

    @Override
    public Book insert(String title, String authorId, Set<String> genresIds) {
        return save(title, authorId, genresIds);
    }

    @Override
    public BookWithoutComments update(String id, String title, String authorId, Set<String> genresIds) {
        return save(id, title, authorId, genresIds);
    }

    @Override
    public BookWithoutComments update(String id, Comment comment) {
        return save(id, comment);
    }

    @Override
    public void deleteById(String id) {
        bookRepository.deleteByIdWithComments(id);
    }

    private BookWithoutComments save(String id, Comment comment) {
        var book = bookRepository.findWithoutCommentsById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(id)));
        bookRepository.addCommentsArrayElementById(comment);
        return book;
    }

    private BookWithoutComments save(String id, String title, String authorId, Set<String> genresIds) {
        if (isEmpty(genresIds)) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }

        var author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %s not found".formatted(authorId)));
        var genres = genreRepository.findAllById(genresIds);
        if (isEmpty(genres) || genresIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds));
        }

        var book = new BookWithoutComments(id, title, author, genres);
        return bookRepository.saveIgnoreComments(book);
    }

    private Book save(String title, String authorId, Set<String> genresIds) {
        if (isEmpty(genresIds)) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }

        var author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %s not found".formatted(authorId)));
        var genres = genreRepository.findAllById(genresIds);
        if (isEmpty(genres) || genresIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds));
        }

        var book = new Book(title, author, genres);
        return bookRepository.save(book);
    }
}
