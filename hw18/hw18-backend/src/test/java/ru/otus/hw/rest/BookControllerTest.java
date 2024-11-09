package ru.otus.hw.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.dto.ShortBookDto;
import ru.otus.hw.services.BookService;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("REST контроллер по работе с книгами ")
@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper mapper;

    private List<BookDto> books;

    private List<ShortBookDto> shortBooks;

    @BeforeEach
    void setUp() {
        List<AuthorDto> authors = List.of(
                new AuthorDto(1L, "Author_1"),
                new AuthorDto(2L, "Author_2")
        );
        List<GenreDto> genres = List.of(
                new GenreDto(1L, "Genre_1"),
                new GenreDto(2L, "Genre_2"),
                new GenreDto(3L, "Genre_3"),
                new GenreDto(4L, "Genre_4")
        );
        books = List.of(
                new BookDto(1, "BookTitle_1",
                        authors.get(0),
                        genres.subList(0, 2)),
                new BookDto(2, "BookTitle_2",
                        authors.get(1),
                        genres.subList(2, 4))
        );
        shortBooks = List.of(
                new ShortBookDto(1L, "BookTitle_1", 1L, List.of(1L, 2L)),
                new ShortBookDto(2L, "BookTitle_2", 2L, List.of(3L, 4L))
        );
    }

    @DisplayName("должен возвращать все книги")
    @Test
    void shouldReturnAllBooks() throws Exception {
        given(bookService.findAll()).willReturn(books);

        mvc.perform(get("/api/v1/book")).andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(books)));
    }

    @DisplayName("должен возвращать книгу по id")
    @Test
    void shouldReturnBookById() throws Exception {
        var book = shortBooks.get(0);

        given(bookService.findShortBookById(1L)).willReturn(Optional.of(book));

        mvc.perform(get("/api/v1/book/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(book)));
    }

    @DisplayName("должен возвращать ошибку если книга не найдена")
    @Test
    void shouldReturnErrorIfBookNotFound() throws Exception {
        given(bookService.findShortBookById(1L)).willReturn(Optional.empty());

        mvc.perform(get("/api/v1/book/{id}", 1L))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Entity not found!")));
    }

    @DisplayName("должен сохранять отредактированную книгу")
    @Test
    void shouldEditBook() throws Exception {
        var book = new ShortBookDto(1L, "AddBook_Title", 3L, List.of(3L, 4L));

        mvc.perform(put("/api/v1/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(book)))
                .andExpect(status().isOk());

        verify(bookService).update(book);
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldAddBook() throws Exception {
        var book = new ShortBookDto(0, "AddBook_Title", 3L, List.of(3L, 4L));

        mvc.perform(post("/api/v1/book", book)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(book)))
                .andExpect(status().isOk());

        verify(bookService).insert(book);
    }

    @DisplayName("должен удалять книгу по id")
    @Test
    void shouldDeleteBook() throws Exception {
        mvc.perform(delete("/api/v1/book/{id}", 1L))
                .andExpect(status().isOk());

        verify(bookService).deleteById(1L);
    }
}
