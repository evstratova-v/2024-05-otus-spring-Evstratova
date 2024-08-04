package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CommentRepository для работы с комментариями ")
@DataMongoTest
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private List<Comment> dbComments;

    private List<Book> dbBooks;

    @BeforeEach
    void setUp() {
        dbComments = mongoTemplate.findAll(Comment.class);
        dbBooks = mongoTemplate.findAll(Book.class);
    }

    @DisplayName("должен возвращать комментарий по id")
    @Test
    void shouldReturnCorrectCommentById() {
        dbComments.forEach(expectedComment -> {
            var actualComment = commentRepository.findById(expectedComment.getId());
            assertThat(actualComment).isPresent().get()
                    .usingRecursiveComparison()
                    .ignoringFields("book")
                    .isEqualTo(expectedComment);

            var actualBookId = actualComment.get().getBook().getId();
            var expectedBookId = expectedComment.getBook().getId();
            assertThat(actualBookId).isEqualTo(expectedBookId);
        });
    }

    @DisplayName("должен возвращать список комментариев по id книги")
    @Test
    void shouldReturnCorrectCommentsByBookId() {
        var book = dbBooks.get(0);
        var expectedComments = Arrays.asList(dbComments.get(0), dbComments.get(1));

        var actualComments = commentRepository.findByBookId(book.getId());

        assertThat(actualComments).isNotEmpty()
                .matches(comments -> comments.size() == expectedComments.size())
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("book")
                .isEqualTo(expectedComments);

        var actualBookId = book.getId();
        var expectedBookId = actualComments.get(0).getBook().getId();
        assertThat(actualBookId).isEqualTo(expectedBookId);

        expectedBookId = actualComments.get(1).getBook().getId();
        assertThat(actualBookId).isEqualTo(expectedBookId);
    }

    @DisplayName("должен сохранять новый комментарий")
    @Test
    void shouldSaveNewComment() {
        var expectedBook = dbBooks.get(0);
        var expectedComment = new Comment("CommentText_1000", expectedBook);

        var savedComment = commentRepository.save(expectedComment);
        assertThat(savedComment).matches(comment -> comment.getId() != null)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedComment);

        var actualComment = mongoTemplate.findById(savedComment.getId(), Comment.class);
        assertThat(actualComment).matches(comment -> comment.getId().equals(savedComment.getId()))
                .matches(comment -> comment.getText().equals(expectedComment.getText()))
                .matches(comment -> comment.getBook().getId().equals(expectedBook.getId()));
    }

    @DisplayName("должен сохранять изменённый комментарий")
    @Test
    void shouldSaveUpdatedComment() {
        var expectedBook = dbBooks.get(2);
        var expectedComment = dbComments.get(0);
        expectedComment.setText("CommentText_1000");
        expectedComment.setBook(expectedBook);

        var savedComment = commentRepository.save(expectedComment);
        assertThat(savedComment).matches(comment -> comment.getId().equals(expectedComment.getId()))
                .matches(comment -> comment.getText().equals(expectedComment.getText()))
                .matches(comment -> comment.getBook().getId().equals(expectedBook.getId()));

        var actualComment = mongoTemplate.findById(savedComment.getId(), Comment.class);
        assertThat(actualComment).matches(comment -> comment.getId().equals(expectedComment.getId()))
                .matches(comment -> comment.getText().equals(expectedComment.getText()))
                .matches(comment -> comment.getBook().getId().equals(expectedBook.getId()));
    }

    @DisplayName("должен удалять комментарий по id")
    @Test
    void shouldDeleteComment() {
        commentRepository.deleteById(dbComments.get(0).getId());
        var deletedComment = mongoTemplate.findById(dbComments.get(0).getId(), Comment.class);
        assertThat(deletedComment).isNull();
    }
}
