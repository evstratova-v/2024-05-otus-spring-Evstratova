package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("Сервис для работы с комментариями ")
@DataMongoTest
@Import({CommentServiceImpl.class})
public class CommentServiceImplTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private MongoTemplate mongoTemplate;

    private List<Book> dbBooks;

    private List<Comment> dbComments;

    @BeforeEach
    void setUp() {
        dbBooks = mongoTemplate.findAll(Book.class);
        dbComments = mongoTemplate.findAll(Comment.class);
    }

    @DisplayName("должен возвращать комментарий по id")
    @Test
    void shouldReturnCorrectCommentById() {
        dbComments.forEach(comment -> {
            var actualComment = commentService.findById(comment.getId());

            assertThat(actualComment).isPresent()
                    .get()
                    .usingRecursiveComparison()
                    .ignoringFields("book")
                    .isEqualTo(comment);
        });
    }

    @DisplayName("должен возвращать список комментариев по id книги")
    @Test
    void shouldReturnCorrectCommentsByBookId() {
        var bookId = dbBooks.get(0).getId();
        var actualComments = commentService.findByBookId(bookId);
        var expectedComments = dbComments.subList(0, 2);

        assertThat(actualComments).isNotEmpty()
                .matches(c -> c.size() == 2)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("book")
                .isEqualTo(expectedComments);
    }

    @DisplayName("должен сохранять новый комментарий")
    @Test
    void shouldSaveNewComment() {
        var expectedComment = new Comment("CommentText_1000", dbBooks.get(0));

        var savedComment = commentService.insert("CommentText_1000", dbBooks.get(0).getId());
        assertThat(savedComment).matches(comment -> comment.getId() != null)
                .usingRecursiveComparison()
                .ignoringFields("id", "book")
                .isEqualTo(expectedComment);

        var actualComment = mongoTemplate.findById(savedComment.getId(), Comment.class);
        assertThat(actualComment).matches(comment -> comment.getId().equals(savedComment.getId()))
                .matches(comment -> comment.getText().equals(expectedComment.getText()))
                .matches(comment -> comment.getBook().getId().equals(dbBooks.get(0).getId()));
    }

    @DisplayName("должен сохранять изменённый комментарий")
    @Test
    void shouldSaveUpdatedComment() {
        var bookWithCommentBefore = dbBooks.get(0);
        var bookForComment = dbBooks.get(2);
        int commentListSize = bookForComment.getComments().size();

        var commentBefore = dbComments.get(0);
        var expectedComment = new Comment("CommentText_1000", dbBooks.get(2));


        var actualComment = commentService.update(commentBefore.getId(), "CommentText_1000", dbBooks.get(2).getId());
        assertThat(actualComment).matches(comment -> comment.getId().equals(commentBefore.getId()))
                .matches(comment -> comment.getText().equals(expectedComment.getText()))
                .matches(comment -> comment.getBook().getId().equals(dbBooks.get(2).getId()));

        var updatedBookWithoutComment = mongoTemplate.findById(bookWithCommentBefore.getId(), Book.class);
        assertThat(updatedBookWithoutComment)
                .matches(book -> book.getComments().size() == bookWithCommentBefore.getComments().size() - 1);


        var updatedBook = mongoTemplate.findById(bookForComment.getId(), Book.class);
        assertThat(updatedBook.getComments())
                .isNotEmpty()
                .matches(comments -> comments.size() == commentListSize + 1);
        var insertedCommentFromBook = updatedBook.getComments().get(commentListSize);

        assertThat(insertedCommentFromBook).matches(comment -> comment.getId().equals(actualComment.getId()))
                .matches(comment -> comment.getText().equals(expectedComment.getText()))
                .matches(comment -> comment.getBook().getId().equals(bookForComment.getId()));
    }

    @DisplayName("должен удалять комментарий по id")
    @Test
    void shouldDeleteComment() {
        String commentId = dbComments.get(0).getId();
        assertThat(commentService.findById(commentId)).isNotEmpty();

        assertThatCode(() -> commentService.deleteById(commentId)).doesNotThrowAnyException();

        assertThat(commentService.findById(commentId)).isEmpty();
    }
}
