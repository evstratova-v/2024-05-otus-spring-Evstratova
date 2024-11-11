package ru.otus.hw.rest;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.ShortBookDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookWithoutComments;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.time.Duration;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DisplayName("REST контроллер по работе с книгами ")
@WebFluxTest(BookController.class)
@ContextConfiguration(classes = BookController.class)
public class BookControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private AuthorRepository authorRepository;

    @MockBean
    private GenreRepository genreRepository;

    private List<BookWithoutComments> booksWithoutComments;

    @BeforeEach
    void setUp() {
        List<Author> authors = getAuthors();
        List<Genre> genres = getGenres();
        booksWithoutComments = getBooksWithoutComments(authors, genres);
    }

    @DisplayName("должен возвращать все книги")
    @Test
    void shouldReturnAllBooks() {
        Flux<BookWithoutComments> booksFlux = Flux.fromIterable(booksWithoutComments);
        given(bookRepository.findAllWithoutCommentsBy()).willReturn(booksFlux);

        var webTestClientForTest = webTestClient.mutate()
                .responseTimeout(Duration.ofSeconds(5))
                .build();

        var result = webTestClientForTest
                .get().uri("/api/v1/book")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(BookDto.class)
                .getResponseBody();

        var step = StepVerifier.create(result);
        StepVerifier.Step<BookDto> stepResult = null;
        List<BookDto> booksDto = booksWithoutComments.stream().map(BookDto::toDto).toList();
        for (BookDto bookDto : booksDto) {
            stepResult = step.expectNext(bookDto);
        }
        stepResult.verifyComplete();
    }

    @DisplayName("должен возвращать книгу по id")
    @Test
    void shouldReturnBookById() {
        BookWithoutComments book = booksWithoutComments.get(0);
        ShortBookDto expectedBook = ShortBookDto.toDto(book);
        String objectId = book.getId();

        given(bookRepository.findWithoutCommentsById(objectId)).willReturn(Mono.just(book));

        var webTestClientForTest = webTestClient.mutate()
                .responseTimeout(Duration.ofSeconds(5))
                .build();

        var result = webTestClientForTest
                .get().uri("/api/v1/book/{id}", objectId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(ShortBookDto.class)
                .getResponseBody();

        StepVerifier.create(result)
                .expectNext(expectedBook)
                .verifyComplete();
    }

    @DisplayName("должен возвращать ошибку если книга не найдена")
    @Test
    void shouldReturnErrorIfBookNotFound() {
        String objectId = new ObjectId().toHexString();

        given(bookRepository.findWithoutCommentsById(objectId)).willReturn(Mono.empty());

        var webTestClientForTest = webTestClient.mutate()
                .responseTimeout(Duration.ofSeconds(5))
                .build();

        webTestClientForTest
                .get().uri("/api/v1/book/{id}", objectId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @DisplayName("должен сохранять отредактированную книгу")
    @Test
    void shouldEditBook() {
        BookWithoutComments book = booksWithoutComments.get(0);
        ShortBookDto bookForSave = ShortBookDto.toDto(book);
        BookDto expectedBook = BookDto.toDto(book);

        given(authorRepository.findById(anyString())).willReturn(Mono.just(book.getAuthor()));
        given(genreRepository.findAllById(anyIterable())).willReturn(Flux.fromIterable(book.getGenres()));
        given(bookRepository.saveIgnoreComments(any())).willReturn(Mono.just(book));

        var webTestClientForTest = webTestClient.mutate()
                .responseTimeout(Duration.ofSeconds(5))
                .build();

        var result = webTestClientForTest
                .put().uri("/api/v1/book")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(bookForSave)
                .exchange()
                .expectStatus().isOk()
                .returnResult(BookDto.class)
                .getResponseBody();

        StepVerifier.create(result)
                .expectNext(expectedBook)
                .verifyComplete();
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldAddBook() {
        String objectId = new ObjectId().toHexString();
        BookWithoutComments bookWithoutComments = booksWithoutComments.get(0);
        Book book = new Book("AddBook_Title",
                bookWithoutComments.getAuthor(),
                bookWithoutComments.getGenres());

        ShortBookDto bookForSave = ShortBookDto.toDto(book);

        book.setId(objectId);
        BookDto expectedBook = BookDto.toDto(book);

        given(authorRepository.findById(anyString())).willReturn(Mono.just(book.getAuthor()));
        given(genreRepository.findAllById(anyIterable())).willReturn(Flux.fromIterable(book.getGenres()));
        given(bookRepository.save(any())).willReturn(Mono.just(book));

        var webTestClientForTest = webTestClient.mutate()
                .responseTimeout(Duration.ofSeconds(5))
                .build();

        var result = webTestClientForTest
                .post().uri("/api/v1/book")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(bookForSave)
                .exchange()
                .expectStatus().isOk()
                .returnResult(BookDto.class)
                .getResponseBody();

        StepVerifier.create(result)
                .expectNext(expectedBook)
                .verifyComplete();
    }

    @DisplayName("должен удалять книгу по id")
    @Test
    void shouldDeleteBook() {
        String objectId = new ObjectId().toHexString();

        var webTestClientForTest = webTestClient.mutate()
                .responseTimeout(Duration.ofSeconds(5))
                .build();

        var result = webTestClientForTest
                .delete().uri("/api/v1/book/{id}", objectId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Void.class)
                .getResponseBody();

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();

        verify(bookRepository).deleteByIdWithComments(objectId);
    }

    private static List<Author> getAuthors() {
        return List.of(
                new Author(new ObjectId().toHexString(), "Author_1"),
                new Author(new ObjectId().toHexString(), "Author_2")
        );
    }

    private static List<Genre> getGenres() {
        return List.of(
                new Genre(new ObjectId().toHexString(), "Genre_1"),
                new Genre(new ObjectId().toHexString(), "Genre_2"),
                new Genre(new ObjectId().toHexString(), "Genre_3"),
                new Genre(new ObjectId().toHexString(), "Genre_4")
        );
    }

    private static List<BookWithoutComments> getBooksWithoutComments(List<Author> authors, List<Genre> genres) {
        return List.of(
                new BookWithoutComments(new ObjectId().toHexString(), "BookTitle_1",
                        authors.get(0),
                        genres.subList(0, 2)),
                new BookWithoutComments(new ObjectId().toHexString(), "BookTitle_2",
                        authors.get(1),
                        genres.subList(2, 4))
        );
    }
}
