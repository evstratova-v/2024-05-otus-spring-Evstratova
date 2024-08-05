package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("Сервис для работы с книгами ")
@DataMongoTest
@Import({BookServiceImpl.class})
public class BookServiceImplTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private MongoTemplate mongoTemplate;

    private List<Author> dbAuthors;

    private List<Genre> dbGenres;

    private List<Book> dbBooks;

    @BeforeEach
    void setUp() {
        dbAuthors = mongoTemplate.findAll(Author.class);
        dbGenres = mongoTemplate.findAll(Genre.class);
        dbBooks = mongoTemplate.findAll(Book.class);
    }

    @DisplayName("должен загружать книгу по id")
    @Test
    void shouldReturnCorrectBookById() {
        dbBooks.forEach(expectedBook -> {
            var actualBook = bookService.findById(expectedBook.getId());

            assertThat(actualBook).isPresent()
                    .get()
                    .usingRecursiveComparison()
                    .ignoringFields("comments")
                    .isEqualTo(expectedBook);
        });
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        var actualBooks = bookService.findAll();
        var expectedBooks = dbBooks;

        assertThat(actualBooks).usingRecursiveFieldByFieldElementComparatorIgnoringFields("comments")
                .isEqualTo(expectedBooks);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        var expectedGenres = List.of(dbGenres.get(0), dbGenres.get(2));
        var actualBook = bookService.insert("BookTitle_10500", dbAuthors.get(0).getId(),
                Set.of(dbGenres.get(0).getId(), dbGenres.get(2).getId()));

        assertThat(actualBook).isNotNull().matches(b -> b.getTitle().equals("BookTitle_10500"))
                .matches(b -> b.getAuthor().equals(dbAuthors.get(0)))
                .matches(b -> b.getGenres().size() == 2)
                .matches(b -> b.getGenres().equals(expectedGenres))
                .matches(b -> b.getComments().isEmpty());
        assertThat(actualBook.getGenres()).usingRecursiveComparison().isEqualTo(expectedGenres);
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var expectedGenres = List.of(dbGenres.get(0), dbGenres.get(2));
        var commentsBeforeUpdate = dbBooks.get(0).getComments();

        var actualBook = bookService.update(dbBooks.get(0).getId(), "BookTitle_10500", dbAuthors.get(0).getId(),
                Set.of(dbGenres.get(0).getId(), dbGenres.get(2).getId()));

        assertThat(actualBook).isNotNull().matches(b -> b.getTitle().equals("BookTitle_10500"))
                .matches(b -> b.getAuthor().equals(dbAuthors.get(0)))
                .matches(b -> b.getGenres().size() == 2)
                .matches(b -> b.getGenres().equals(expectedGenres));

        assertThat(actualBook.getGenres())
                .usingRecursiveComparison()
                .isEqualTo(expectedGenres);

        var actualBookWithComments = mongoTemplate.findById(actualBook.getId(), Book.class);
        assertThat(actualBookWithComments.getComments())
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("book")
                .isEqualTo(commentsBeforeUpdate);
    }

    @DisplayName("должен удалять книгу по id")
    @Test
    void shouldDeleteBook() {
        var book = dbBooks.get(0);
        assertThat(bookService.findById(book.getId())).isNotEmpty();

        assertThatCode(() -> bookService.deleteById(book.getId())).doesNotThrowAnyException();

        assertThat(bookService.findById(book.getId())).isEmpty();
    }
}
