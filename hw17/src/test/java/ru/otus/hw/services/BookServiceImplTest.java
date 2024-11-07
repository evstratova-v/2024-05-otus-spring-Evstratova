package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.config.CachingConfig;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.dto.ShortBookDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("Сервис для работы с книгами ")
@DataJpaTest
@Import({BookServiceImpl.class, CachingConfig.class})
@Transactional(propagation = Propagation.NEVER)
public class BookServiceImplTest {

    private static final long FIRST_BOOK_ID = 1L;

    @Autowired
    private BookService bookService;

    @Autowired
    private CacheManager cacheManager;

    private List<Author> dbAuthors;

    private List<Genre> dbGenres;

    private List<BookDto> dtoBooks;

    @BeforeEach
    void setUp() {
        dbAuthors = getDbAuthors();
        dbGenres = getDbGenres();
        dtoBooks = getDtoBooks(dbAuthors, dbGenres);
    }

    @DisplayName("должен загружать книгу по id")
    @ParameterizedTest
    @MethodSource("getShortBookDto")
    void shouldReturnCorrectBookById(ShortBookDto expectedShortBookDto) {
        var actualShortBookDto = bookService.findShortBookById(expectedShortBookDto.getId());

        assertThat(actualShortBookDto).isPresent()
                .get()
                .isEqualTo(expectedShortBookDto);
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        var actualBooksDto = bookService.findAll();
        var expectedBooksDto = dtoBooks;

        assertThat(actualBooksDto).isEqualTo(expectedBooksDto);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        var shortBookDto = new ShortBookDto(0L, "BookTitle_10500", dbAuthors.get(0).getId(),
                List.of(dbGenres.get(0).getId(), dbGenres.get(2).getId()));
        var actualBookDto = bookService.insert(shortBookDto);

        var expectedBookDto = new BookDto(dtoBooks.size() + 1, "BookTitle_10500",
                AuthorDto.toDto(dbAuthors.get(0)),
                List.of(GenreDto.toDto(dbGenres.get(0)), GenreDto.toDto(dbGenres.get(2))));
        assertThat(actualBookDto).isEqualTo(expectedBookDto);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var shortBookDto = new ShortBookDto(FIRST_BOOK_ID, "BookTitle_10500", dbAuthors.get(0).getId(),
                List.of(dbGenres.get(0).getId(), dbGenres.get(2).getId()));
        var actualBookDto = bookService.update(shortBookDto);


        var expectedBookDto = new BookDto(FIRST_BOOK_ID, "BookTitle_10500",
                AuthorDto.toDto(dbAuthors.get(0)),
                List.of(GenreDto.toDto(dbGenres.get(0)), GenreDto.toDto(dbGenres.get(2))));

        assertThat(actualBookDto).isEqualTo(expectedBookDto);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @DisplayName("должен удалять книгу по id")
    @Test
    void shouldDeleteBook() {
        assertThat(bookService.findShortBookById(FIRST_BOOK_ID)).isNotEmpty();

        assertThatCode(() -> bookService.deleteById(FIRST_BOOK_ID)).doesNotThrowAnyException();

        assertThat(bookService.findShortBookById(FIRST_BOOK_ID)).isEmpty();
    }

    private static List<Author> getDbAuthors() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Author(id, "Author_" + id))
                .toList();
    }

    private static List<Genre> getDbGenres() {
        return IntStream.range(1, 7).boxed()
                .map(id -> new Genre(id, "Genre_" + id))
                .toList();
    }

    private static List<Book> getDbBooks(List<Author> dbAuthors, List<Genre> dbGenres) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Book(id,
                        "BookTitle_" + id,
                        dbAuthors.get(id - 1),
                        dbGenres.subList((id - 1) * 2, (id - 1) * 2 + 2)
                ))
                .toList();
    }

    private static List<BookDto> getDtoBooks(List<Author> dbAuthors, List<Genre> dbGenres) {
        return getDbBooks(dbAuthors, dbGenres).stream().map(BookDto::toDto).toList();
    }

    private static List<ShortBookDto> getShortBookDto() {
        var dbAuthors = getDbAuthors();
        var dbGenres = getDbGenres();
        return getDbBooks(dbAuthors, dbGenres).stream().map(ShortBookDto::toDto).toList();
    }
}
