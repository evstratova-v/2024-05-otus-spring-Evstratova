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
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.ShortBookDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.BookService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping("/api/v1/book")
    public List<BookDto> getAllBooks() {
        return bookService.findAll();
    }

    @GetMapping("/api/v1/book/{id}")
    public ShortBookDto getBookForEdit(@PathVariable("id") long id) {
        return bookService.findShortBookById(id).orElseThrow(EntityNotFoundException::new);
    }

    @PutMapping("/api/v1/book")
    public BookDto editBook(@RequestBody @Valid ShortBookDto shortBookDto) {
        return bookService.update(shortBookDto);
    }

    @PostMapping("/api/v1/book")
    public BookDto addBook(@RequestBody @Valid ShortBookDto shortBookDto) {
        return bookService.insert(shortBookDto);
    }

    @DeleteMapping("/api/v1/book/{id}")
    public void deleteBook(@PathVariable("id") long id) {
        bookService.deleteById(id);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException ex) {
        return ResponseEntity.badRequest().body("Entity not found! " + ex.getMessage());
    }
}
