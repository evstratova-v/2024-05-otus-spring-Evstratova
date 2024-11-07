package ru.otus.hw.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.dto.ShortBookDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@DisplayName("Контроллер по работе с книгами ")
@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private GenreService genreService;

    private List<AuthorDto> authors;

    private List<GenreDto> genres;

    private List<BookDto> books;

    private List<ShortBookDto> shortBooks;

    @BeforeEach
    void setUp() {
        authors = List.of(
                new AuthorDto(1L, "Author_1"),
                new AuthorDto(1L, "Author_2")
        );
        genres = List.of(
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

    @DisplayName("должен возвращать страницу со списком всех книг")
    @Test
    void shouldReturnListPage() throws Exception {
        given(bookService.findAll()).willReturn(books);

        mvc.perform(get("/")).andExpect(status().isOk())
                .andExpect(model().attribute("books", books))
                .andExpect(view().name("list"));
    }

    @DisplayName("должен возвращать страницу редактирования книги")
    @Test
    void shouldReturnEditPage() throws Exception {
        var book = shortBooks.get(0);

        given(bookService.findShortBookById(1L)).willReturn(Optional.of(book));
        given(authorService.findAll()).willReturn(authors);
        given(genreService.findAll()).willReturn(genres);

        mvc.perform(get("/edit/{id}", 1L)).andExpect(status().isOk())
                .andExpect(model().attribute("book", book))
                .andExpect(model().attribute("allAuthors", authors))
                .andExpect(model().attribute("allGenres", genres))
                .andExpect(view().name("edit"));
    }

    @DisplayName("должен возвращать страницу c ошибкой если книга не найдена")
    @Test
    void shouldReturnErrorPage() throws Exception {
        given(bookService.findShortBookById(1L)).willReturn(Optional.empty());

        mvc.perform(get("/edit/{id}", 1L))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("Book not found!"));
    }

    @DisplayName("должен сохранять отредактированную книгу")
    @Test
    void shouldEditBook() throws Exception {
        var book = shortBooks.get(0);

        mvc.perform(post("/edit", 1L)
                        .param("id", "1")
                        .flashAttr("book", book))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(bookService).update(book);
    }

    @DisplayName("должен возвращать страницу добавления новой книги")
    @Test
    void shouldReturnAddPage() throws Exception {
        given(authorService.findAll()).willReturn(authors);
        given(genreService.findAll()).willReturn(genres);

        mvc.perform(get("/add"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("book", new ShortBookDto()))
                .andExpect(model().attribute("allAuthors", authors))
                .andExpect(model().attribute("allGenres", genres))
                .andExpect(view().name("add"));
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldAddBook() throws Exception {
        var book = new ShortBookDto(0, "AddBook_Title", 3L,
                List.of(genres.get(2).getId(), genres.get(3).getId()));

        mvc.perform(post("/add").flashAttr("book", book))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(bookService).insert(book);
    }

    @DisplayName("должен удалять книгу")
    @Test
    void shouldDeleteBook() throws Exception {
        mvc.perform(post("/delete").param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(bookService).deleteById(1L);
    }
}
