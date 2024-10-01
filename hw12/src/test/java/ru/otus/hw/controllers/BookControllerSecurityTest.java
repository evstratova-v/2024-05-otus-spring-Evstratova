package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.ShortBookDto;
import ru.otus.hw.security.SecurityConfiguration;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Контроллер по работе с книгами c включённой безопасностью ")
@WebMvcTest(BookController.class)
@Import(SecurityConfiguration.class)
public class BookControllerSecurityTest {

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private GenreService genreService;

    @Autowired
    private MockMvc mvc;

    @WithMockUser(roles = "ADMIN")
    @DisplayName("должен предоставлять доступ к ресурсам для пользователя с ролью ADMIN, method = GET")
    @Test
    void shouldStatusOkForAuthenticatedUser() throws Exception {
        var book = new ShortBookDto(1L, "BookTitle_1", 1L, List.of(1L, 2L));
        given(bookService.findShortBookById(1L)).willReturn(Optional.of(book));

        mvc.perform(get("/"))
                .andExpect(status().isOk());
        mvc.perform(get("/edit/{id}", 1L))
                .andExpect(status().isOk());
        mvc.perform(get("/add"))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = "ADMIN")
    @DisplayName("должен предоставлять доступ к ресурсам для пользователя с ролью ADMIN, method = POST")
    @Test
    void shouldStatusRedirectionForAuthenticatedUser() throws Exception {
        var editBook = new ShortBookDto(1L, "BookTitle_1", 1L, List.of(1L, 2L));
        var addBook = new ShortBookDto(0, "AddBook_Title", 3L, List.of(3L, 4L));

        mvc.perform(post("/edit")
                        .param("id", "1")
                        .flashAttr("book", editBook))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        mvc.perform(post("/add")
                        .flashAttr("book", addBook))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        mvc.perform(post("/delete")
                        .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/", "/edit/1", "/add"})
    @DisplayName("должен запрещать доступ к ресурсам без аутентификации, method = GET")
    void shouldRedirectGetToLoginPageForNotAuthenticatedUser(String urlTemplate) throws Exception {
        mvc.perform(get(urlTemplate))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        verifyNoMoreInteractions(bookService);
        verifyNoMoreInteractions(authorService);
        verifyNoMoreInteractions(genreService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/edit", "/add", "/delete"})
    @DisplayName("должен запрещать доступ к ресурсам без аутентификации, method = POST")
    void shouldRedirectPostToLoginPageForNotAuthenticatedUser(String urlTemplate) throws Exception {
        mvc.perform(post(urlTemplate))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        verifyNoMoreInteractions(bookService);
        verifyNoMoreInteractions(authorService);
        verifyNoMoreInteractions(genreService);
    }
}
