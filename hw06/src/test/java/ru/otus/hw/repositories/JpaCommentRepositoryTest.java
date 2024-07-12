package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с комментариями ")
@DataJpaTest
@Import(JpaCommentRepository.class)
public class JpaCommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TestEntityManager em;

    private List<Comment> dbComments;

    @BeforeEach
    void setUp() {
        List<Author> dbAuthors = getDbAuthors();
        List<Genre> dbGenres = getDbGenres();
        List<Book> dbBooks = getDbBooks(dbAuthors, dbGenres);
        dbComments = getDbComments(dbBooks);
    }

    @DisplayName("должен возвращать комментарий по id")
    @ParameterizedTest
    @MethodSource("getDbComments")
    void shouldReturnCorrectCommentById(Comment expectedComment) {
        var actualComment = commentRepository.findById(expectedComment.getId());

        assertThat(actualComment).isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringFields("book")
                .isEqualTo(expectedComment);

        var actualBook = actualComment.get().getBook();
        var expectedBook = actualComment.get().getBook();
        assertThat(actualBook)
                .matches(book -> book.getId() == expectedBook.getId())
                .matches(book -> book.getAuthor().equals(expectedBook.getAuthor()))
                .matches(book -> book.getGenres().size() == book.getGenres().size());
        assertThat(actualBook.getGenres()).usingRecursiveComparison().isEqualTo(expectedBook.getGenres());
    }

    @DisplayName("должен возвращать список комментариев по id книги")
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void shouldReturnCorrectCommentsByBookId(int bookId) {
        var actualComments = commentRepository.findByBookId(bookId);
        var expectedComments = dbComments.subList((bookId - 1) * 2, (bookId - 1) * 2 + 2);

        assertThat(actualComments).isNotEmpty()
                .matches(comments -> comments.size() == expectedComments.size())
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("book")
                .isEqualTo(expectedComments);

        for (int i = 0; i < expectedComments.size(); i++) {
            var actualBook = actualComments.get(i).getBook();
            var expectedBook = expectedComments.get(i).getBook();
            assertThat(actualBook)
                    .matches(book -> book.getId() == expectedBook.getId())
                    .matches(book -> book.getAuthor().equals(expectedBook.getAuthor()))
                    .matches(book -> book.getGenres().size() == book.getGenres().size());
            assertThat(actualBook.getGenres()).usingRecursiveComparison().isEqualTo(expectedBook.getGenres());
        }
    }

    @DisplayName("должен сохранять новый комментарий")
    @Test
    void shouldSaveNewComment() {
        Book expectedBook = em.find(Book.class, 1L);
        Comment expectedComment = new Comment(0L, "CommentText_1000", expectedBook);

        var savedComment = commentRepository.save(expectedComment);
        assertThat(savedComment).matches(comment -> comment.getId() > 0)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedComment);
        em.detach(savedComment);

        var actualComment = em.find(Comment.class, savedComment.getId());
        assertThat(actualComment).matches(comment -> comment.getId() == savedComment.getId())
                .matches(comment -> comment.getText().equals(expectedComment.getText()))
                .matches(comment -> comment.getBook().equals(expectedBook));
    }

    @DisplayName("должен сохранять изменённый комментарий")
    @Test
    void shouldSaveUpdatedComment() {
        Book expectedBook = em.find(Book.class, 3L);
        Comment expectedComment = new Comment(1L, "CommentText_1000", expectedBook);

        var savedComment = commentRepository.save(expectedComment);
        assertThat(savedComment).matches(comment -> comment.getId() == expectedComment.getId())
                .matches(comment -> comment.getText().equals(expectedComment.getText()))
                .matches(comment -> comment.getBook().equals(expectedBook));

        em.flush();
        em.detach(savedComment);

        var actualComment = em.find(Comment.class, savedComment.getId());
        assertThat(actualComment).matches(comment -> comment.getId() == expectedComment.getId())
                .matches(comment -> comment.getText().equals(expectedComment.getText()))
                .matches(comment -> comment.getBook().equals(expectedBook));
    }

    @DisplayName("должен удалять комментарий по id")
    @Test
    void shouldDeleteComment() {
        var firstComment = em.find(Comment.class, 1L);
        assertThat(firstComment).isNotNull();
        em.detach(firstComment);

        commentRepository.deleteById(1L);
        var deletedComment = em.find(Comment.class, 1L);
        assertThat(deletedComment).isNull();
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

    private static List<Comment> getDbComments(List<Book> dbBooks) {
        return IntStream.range(1, 7).boxed()
                .map(id -> new Comment(id,
                        "CommentText_" + id,
                        dbBooks.get((id - 1) / 2)
                ))
                .toList();
    }

    private static List<Comment> getDbComments() {
        var dbAuthors = getDbAuthors();
        var dbGenres = getDbGenres();
        var dbBooks = getDbBooks(dbAuthors, dbGenres);
        return getDbComments(dbBooks);
    }
}
