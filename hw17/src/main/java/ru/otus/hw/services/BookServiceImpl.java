package ru.otus.hw.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.ShortBookDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final CacheManager cacheManager;

    @CircuitBreaker(name = "bookServiceCircuitBreaker", fallbackMethod = "findShortBookByIdInCache")
    @Transactional(readOnly = true)
    @Override
    public Optional<ShortBookDto> findShortBookById(long id) {
        Optional<BookDto> book = bookRepository.findById(id).map(BookDto::toDto);
        book.ifPresent(value -> cacheManager.getCache("books").put(id, value));
        return book.map(ShortBookDto::toDto);
    }

    @CircuitBreaker(name = "bookServiceCircuitBreaker", fallbackMethod = "findBooksInCache")
    @Transactional(readOnly = true)
    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream().map(BookDto::toDto).toList();
    }

    @Transactional
    @Override
    public BookDto insert(ShortBookDto shortBookDto) {
        return BookDto.toDto(save(shortBookDto));
    }

    @Transactional
    @Override
    public BookDto update(ShortBookDto shortBookDto) {
        return BookDto.toDto(save(shortBookDto));
    }

    @CacheEvict(cacheNames = "books", key = "#id")
    @Transactional
    @Override
    public void deleteById(long id) {
        bookRepository.deleteById(id);
    }

    public Optional<ShortBookDto> findShortBookByIdInCache(long id, Throwable t) {
        log.error("error while finding book by id: {}", t.getMessage());
        Cache cache = cacheManager.getCache("books");
        return Optional.ofNullable(cache.get(id, BookDto.class)).map(ShortBookDto::toDto);
    }

    @SuppressWarnings("unchecked")
    public List<BookDto> findBooksInCache(Throwable t) {
        log.error("error while finding all books: {}", t.getMessage());
        Cache cache = cacheManager.getCache("books");
        ConcurrentHashMap<Long, BookDto> books = ((ConcurrentHashMap<Long, BookDto>) cache.getNativeCache());
        return books.values().stream().toList();
    }

    private Book save(ShortBookDto shortBookDto) {
        long authorId = shortBookDto.getAuthorId();
        var author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorId)));

        var genresIds = shortBookDto.getGenresIds();
        var genres = genreRepository.findAllById(genresIds);
        if (isEmpty(genres) || genresIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds));
        }

        var book = new Book(shortBookDto.getId(), shortBookDto.getTitle(), author, genres);
        book = bookRepository.save(book);
        cacheManager.getCache("books").put(book.getId(), BookDto.toDto(book));
        return book;
    }
}
