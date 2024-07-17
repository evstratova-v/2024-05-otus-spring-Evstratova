package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с книгами ")
@DataJpaTest
@Import({JpaBookRepository.class, JpaGenreRepository.class})
class JpaBookRepositoryTest {

    private static final long FIRST_BOOK_ID = 1L;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager em;

    private List<Author> dbAuthors;

    private List<Genre> dbGenres;

    private List<Book> dbBooks;

    @BeforeEach
    void setUp() {
        dbAuthors = getDbAuthors();
        dbGenres = getDbGenres();
        dbBooks = getDbBooks(dbAuthors, dbGenres);
    }

    @DisplayName("должен загружать книгу по id")
    @ParameterizedTest
    @MethodSource("getDbBooks")
    void shouldReturnCorrectBookById(Book expectedBook) {
        var actualBook = bookRepository.findById(expectedBook.getId());
        assertThat(actualBook).isPresent()
                .get()
                .matches(b -> b.getTitle().equals(expectedBook.getTitle()))
                .matches(b -> b.getAuthor().equals(expectedBook.getAuthor()));

        var expectedGenres = expectedBook.getGenres();
        assertThat(actualBook.get().getGenres()).usingRecursiveComparison().isEqualTo(expectedGenres);
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        var actualBooks = bookRepository.findAll();
        var expectedBooks = dbBooks;

        assertThat(actualBooks).usingRecursiveComparison().isEqualTo(expectedBooks);
        actualBooks.forEach(System.out::println);
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        var expectedGenres = List.of(em.find(Genre.class, 1), em.find(Genre.class, 3));
        var expectedBook = new Book(0, "BookTitle_10500", dbAuthors.get(0), expectedGenres);
        var returnedBook = bookRepository.save(expectedBook);

        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);
        em.detach(returnedBook);

        var actualBook = em.find(Book.class, returnedBook.getId());
        assertThat(actualBook).isNotNull().matches(b -> b.getTitle().equals("BookTitle_10500"))
                .matches(b -> b.getAuthor().equals(dbAuthors.get(0)))
                .matches(b -> b.getGenres().size() == 2);
        assertThat(actualBook.getGenres()).usingRecursiveComparison().isEqualTo(expectedGenres);
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var expectedBook = new Book(FIRST_BOOK_ID, "BookTitle_10500", dbAuthors.get(2),
                List.of(dbGenres.get(4), dbGenres.get(5)));

        var firstBook = em.find(Book.class, FIRST_BOOK_ID);
        String oldTitle = firstBook.getTitle();
        Author oldAuthor = firstBook.getAuthor();
        List<Genre> oldGenres = List.of(dbGenres.get(0), dbGenres.get(1));
        assertThat(firstBook.getGenres()).usingRecursiveComparison().isEqualTo(oldGenres);
        em.detach(firstBook);

        var savedBook = bookRepository.save(expectedBook);
        assertThat(savedBook.getTitle()).isNotEqualTo(oldTitle).isEqualTo("BookTitle_10500");
        assertThat(savedBook.getAuthor()).isNotEqualTo(oldAuthor).isEqualTo(dbAuthors.get(2));
        assertThat(savedBook.getGenres()).usingRecursiveComparison()
                .isNotEqualTo(oldGenres)
                .isEqualTo(List.of(dbGenres.get(4), dbGenres.get(5)));

        em.flush();
        em.detach(savedBook);

        var actualBook = em.find(Book.class, FIRST_BOOK_ID);
        assertThat(actualBook.getTitle()).isNotEqualTo(oldTitle).isEqualTo("BookTitle_10500");
        assertThat(actualBook.getAuthor()).isNotEqualTo(oldAuthor).isEqualTo(dbAuthors.get(2));
        assertThat(actualBook.getGenres()).usingRecursiveComparison()
                .isNotEqualTo(oldGenres)
                .isEqualTo(List.of(dbGenres.get(4), dbGenres.get(5)));
    }

    @DisplayName("должен удалять книгу по id ")
    @Test
    void shouldDeleteBook() {
        var firstBook = em.find(Book.class, FIRST_BOOK_ID);
        assertThat(firstBook).isNotNull();
        em.detach(firstBook);

        bookRepository.deleteById(FIRST_BOOK_ID);
        var deletedBook = em.find(Book.class, FIRST_BOOK_ID);
        assertThat(deletedBook).isNull();
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

    private static List<Book> getDbBooks() {
        var dbAuthors = getDbAuthors();
        var dbGenres = getDbGenres();
        return getDbBooks(dbAuthors, dbGenres);
    }
}