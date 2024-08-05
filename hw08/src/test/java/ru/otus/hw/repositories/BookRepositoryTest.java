package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BookRepositoryTest для работы с книгами с listener-ами в контексте ")
@DataMongoTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

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
            var actualBook = bookRepository.findById(expectedBook.getId());
            assertThat(actualBook).isPresent()
                    .get()
                    .matches(b -> b.getTitle().equals(expectedBook.getTitle()))
                    .matches(b -> b.getAuthor().equals(expectedBook.getAuthor()));

            var expectedGenres = expectedBook.getGenres();
            assertThat(actualBook.get().getGenres()).usingRecursiveComparison().isEqualTo(expectedGenres);
        });
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        var actualBooks = bookRepository.findAll();
        var expectedBooks = dbBooks;

        assertThat(actualBooks)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("comments")
                .isEqualTo(expectedBooks);
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        var expectedGenres = List.of(dbGenres.get(2), dbGenres.get(3));
        var expectedBook = new Book("BookTitle_10500", dbAuthors.get(0), expectedGenres);

        var returnedBook = bookRepository.save(expectedBook);
        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() != null)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedBook);

        var actualBook = mongoTemplate.findById(returnedBook.getId(), Book.class);
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
        var book = dbBooks.get(0);

        String oldTitle = book.getTitle();
        Author oldAuthor = book.getAuthor();
        List<Genre> oldGenres = List.of(dbGenres.get(0), dbGenres.get(1));

        book.setTitle("BookTitle_10500");
        book.setAuthor(dbAuthors.get(2));
        book.setGenres(List.of(dbGenres.get(4), dbGenres.get(5)));

        var savedBook = bookRepository.save(book);
        assertThat(savedBook.getTitle()).isNotEqualTo(oldTitle).isEqualTo("BookTitle_10500");
        assertThat(savedBook.getAuthor()).isNotEqualTo(oldAuthor).isEqualTo(dbAuthors.get(2));
        assertThat(savedBook.getGenres()).usingRecursiveComparison()
                .isNotEqualTo(oldGenres)
                .isEqualTo(List.of(dbGenres.get(4), dbGenres.get(5)));

        var actualBook = mongoTemplate.findById(book.getId(), Book.class);

        assertThat(actualBook.getTitle()).isNotEqualTo(oldTitle).isEqualTo("BookTitle_10500");
        assertThat(actualBook.getAuthor()).isNotEqualTo(oldAuthor).isEqualTo(dbAuthors.get(2));
        assertThat(actualBook.getGenres()).usingRecursiveComparison()
                .isNotEqualTo(oldGenres)
                .isEqualTo(List.of(dbGenres.get(4), dbGenres.get(5)));
    }

    @DisplayName("должен удалять книгу по id ")
    @Test
    void shouldDeleteBook() {
        var book = dbBooks.get(0);
        var commentsBeforeDelete = mongoTemplate.findAll(Comment.class);
        List<String> commentsIds = book.getComments().stream().map(Comment::getId).toList();

        bookRepository.deleteByIdWithComments(book.getId());
        var deletedBook = mongoTemplate.findById(book.getId(), Book.class);
        assertThat(deletedBook).isNull();

        var commentsAfterDelete = mongoTemplate.findAll(Comment.class);
        assertThat(commentsAfterDelete)
                .matches(comments -> commentsBeforeDelete.size() - commentsIds.size() == comments.size());
        commentsIds.forEach(commentId -> assertThat(mongoTemplate.findById(commentId, Comment.class)).isNull());
    }
}
