package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.JpaBookRepository;
import ru.otus.hw.repositories.JpaCommentRepository;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("Сервис для работы с комментариями ")
@DataJpaTest
@Import({CommentServiceImpl.class, JpaCommentRepository.class, JpaBookRepository.class})
@Transactional(propagation = Propagation.NEVER)
public class CommentServiceImplTest {

    @Autowired
    private CommentService commentService;

    private List<Book> dbBooks;

    private List<CommentDto> commentsDto;

    @BeforeEach
    void setUp() {
        List<Author> dbAuthors = getDbAuthors();
        List<Genre> dbGenres = getDbGenres();
        dbBooks = getDbBooks(dbAuthors, dbGenres);
        commentsDto = getCommentsDto(dbBooks);
    }

    @DisplayName("должен возвращать комментарий по id")
    @ParameterizedTest
    @MethodSource("getCommentsDto")
    void shouldReturnCorrectCommentById(CommentDto expectedCommentDto) {
        var actualCommentDto = commentService.findById(expectedCommentDto.getId());

        assertThat(actualCommentDto).isPresent()
                .get()
                .isEqualTo(expectedCommentDto);
    }

    @DisplayName("должен возвращать список комментариев по id книги")
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void shouldReturnCorrectCommentsByBookId(int bookId) {
        var actualCommentsDto = commentService.findByBookId(bookId);
        var expectedCommentsDto = commentsDto.subList((bookId - 1) * 2, (bookId - 1) * 2 + 2);

        assertThat(actualCommentsDto).isNotEmpty()
                .matches(c -> c.size() == 2)
                .isEqualTo(expectedCommentsDto);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @DisplayName("должен сохранять новый комментарий")
    @Test
    void shouldSaveNewComment() {
        var actualCommentDto = commentService.insert("CommentText_1000", dbBooks.get(0).getId());
        var expectedCommentDto = new CommentDto(commentsDto.size() + 1, "CommentText_1000");

        assertThat(actualCommentDto).isEqualTo(expectedCommentDto);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @DisplayName("должен сохранять изменённый комментарий")
    @Test
    void shouldSaveUpdatedComment() {
        var actualCommentDto = commentService.update(1L, "CommentText_1000", dbBooks.get(2).getId());
        var expectedCommentDto = new CommentDto(1L, "CommentText_1000");

        assertThat(actualCommentDto).isEqualTo(expectedCommentDto);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @DisplayName("должен удалять комментарий по id")
    @Test
    void shouldDeleteComment() {
        assertThat(commentService.findById(1L)).isNotEmpty();

        assertThatCode(() -> commentService.deleteById(1L)).doesNotThrowAnyException();

        assertThat(commentService.findById(1L)).isEmpty();
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

    private static List<CommentDto> getCommentsDto(List<Book> dbBooks) {
        return IntStream.range(1, 7).boxed()
                .map(id -> new Comment(id,
                        "CommentText_" + id,
                        dbBooks.get((id - 1) / 2)
                ))
                .map(CommentDto::toDto)
                .toList();
    }

    private static List<CommentDto> getCommentsDto() {
        var dbAuthors = getDbAuthors();
        var dbGenres = getDbGenres();
        var dbBooks = getDbBooks(dbAuthors, dbGenres);
        return getCommentsDto(dbBooks);
    }
}
