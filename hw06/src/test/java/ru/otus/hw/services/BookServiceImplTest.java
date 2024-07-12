package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.JpaAuthorRepository;
import ru.otus.hw.repositories.JpaBookRepository;
import ru.otus.hw.repositories.JpaGenreRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("Сервис для работы с книгами ")
@DataJpaTest
@Import({BookServiceImpl.class, JpaAuthorRepository.class, JpaGenreRepository.class, JpaBookRepository.class})
@Transactional(propagation = Propagation.NEVER)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookServiceImplTest {

    private static final long FIRST_BOOK_ID = 1L;

    @Autowired
    private BookService bookService;

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
    @MethodSource("getDtoBooks")
    void shouldReturnCorrectBookById(BookDto expectedBookDto) {
        var actualBookDto = bookService.findById(expectedBookDto.getId());

        assertThat(actualBookDto).isPresent()
                .get()
                .isEqualTo(expectedBookDto);
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        var actualBooksDto = bookService.findAll();
        var expectedBooksDto = dtoBooks;

        assertThat(actualBooksDto).isEqualTo(expectedBooksDto);
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        var actualBookDto = bookService.insert("BookTitle_10500", dbAuthors.get(0).getId(),
                Set.of(dbGenres.get(0).getId(), dbGenres.get(2).getId()));

        var expectedBookDto = new BookDto(dtoBooks.size() + 1, "BookTitle_10500", dbAuthors.get(0).getId(),
                dbAuthors.get(0).getFullName(), Map.of(
                dbGenres.get(0).getId(), dbGenres.get(0).getName(),
                dbGenres.get(2).getId(), dbGenres.get(2).getName()));
        assertThat(actualBookDto).isEqualTo(expectedBookDto);
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var actualBookDto = bookService.update(FIRST_BOOK_ID, "BookTitle_10500", dbAuthors.get(0).getId(),
                Set.of(dbGenres.get(0).getId(), dbGenres.get(2).getId()));


        var expectedBookDto = new BookDto(FIRST_BOOK_ID, "BookTitle_10500", dbAuthors.get(0).getId(),
                dbAuthors.get(0).getFullName(), Map.of(
                dbGenres.get(0).getId(), dbGenres.get(0).getName(),
                dbGenres.get(2).getId(), dbGenres.get(2).getName()));

        assertThat(actualBookDto).isEqualTo(expectedBookDto);
    }

    @DisplayName("должен удалять книгу по id")
    @Test
    void shouldDeleteBook() {
        assertThat(bookService.findById(FIRST_BOOK_ID)).isNotEmpty();

        assertThatCode(() -> bookService.deleteById(FIRST_BOOK_ID)).doesNotThrowAnyException();

        assertThat(bookService.findById(FIRST_BOOK_ID)).isEmpty();
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

    private static List<BookDto> getDtoBooks(List<Author> dbAuthors, List<Genre> dbGenres) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Book(id,
                        "BookTitle_" + id,
                        dbAuthors.get(id - 1),
                        dbGenres.subList((id - 1) * 2, (id - 1) * 2 + 2)
                ))
                .map(BookDto::toDto)
                .toList();
    }

    private static List<BookDto> getDtoBooks() {
        var dbAuthors = getDbAuthors();
        var dbGenres = getDbGenres();
        return getDtoBooks(dbAuthors, dbGenres);
    }
}
