package ru.otus.hw.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.ShortBookDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookWithoutComments;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookController {

    private final BookRepository bookRepository;

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    @GetMapping("/api/v1/book")
    public Flux<BookDto> getAllBooks() {
        return bookRepository.findAllWithoutCommentsBy().map(BookDto::toDto);
    }

    @GetMapping("/api/v1/book/{id}")
    public Mono<ResponseEntity<ShortBookDto>> getBookForEdit(@PathVariable("id") String id) {
        return bookRepository.findWithoutCommentsById(id).map(ShortBookDto::toDto)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.fromCallable(() -> ResponseEntity.notFound().build()));
    }

    @PutMapping("/api/v1/book")
    public Mono<ResponseEntity<BookDto>> editBook(@RequestBody @Valid ShortBookDto shortBookDto) {
        Mono<Author> author = findAuthorById(shortBookDto.getAuthorId());
        Mono<List<Genre>> genres = findGenresByIds(shortBookDto.getGenresIds());

        return Mono.zip(Mono.just(shortBookDto), author, genres)
                .flatMap(data -> bookRepository.saveIgnoreComments(
                        new BookWithoutComments(
                                data.getT1().getId(), data.getT1().getTitle(), data.getT2(), data.getT3()
                        )
                ))
                .map(BookDto::toDto)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.fromCallable(() -> ResponseEntity.badRequest().build()));
    }

    @PostMapping("/api/v1/book")
    public Mono<ResponseEntity<BookDto>> addBook(@RequestBody @Valid ShortBookDto shortBookDto) {
        Mono<Author> author = findAuthorById(shortBookDto.getAuthorId());
        Mono<List<Genre>> genres = findGenresByIds(shortBookDto.getGenresIds());

        return Mono.zip(Mono.just(shortBookDto), author, genres)
                .flatMap(data -> bookRepository.save(
                        new Book(data.getT1().getTitle(), data.getT2(), data.getT3())
                ))
                .map(BookDto::toDto)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.fromCallable(() -> ResponseEntity.badRequest().build()));
    }

    @DeleteMapping("/api/v1/book/{id}")
    public Mono<Void> deleteBook(@PathVariable("id") String id) {
        return bookRepository.deleteByIdWithComments(id);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public Mono<ResponseEntity<String>> handleEntityNotFound(EntityNotFoundException ex) {
        return Mono.fromCallable(() -> ResponseEntity.badRequest().body("Entity not found! " + ex.getMessage()));
    }

    private Mono<Author> findAuthorById(String id) {
        return authorRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new EntityNotFoundException("Author with id %s not found".formatted(id))
                ));
    }

    private Mono<List<Genre>> findGenresByIds(List<String> genresIds) {
        return genreRepository.findAllById(genresIds).collectList()
                .filter(list -> list.size() == genresIds.size())
                .switchIfEmpty(Mono.error(
                        new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds))
                ));
    }
}
