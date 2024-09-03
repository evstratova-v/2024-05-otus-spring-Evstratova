package ru.otus.hw.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("REST контроллер по работе с авторами ")
@WebMvcTest(AuthorController.class)
public class AuthorControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthorService authorService;

    @Autowired
    private ObjectMapper mapper;

    @DisplayName("должен возвращать всех авторов")
    @Test
    void shouldReturnAllAuthors() throws Exception {
        List<AuthorDto> authors = List.of(
                new AuthorDto(1L, "Author_1"),
                new AuthorDto(2L, "Author_2")
        );

        given(authorService.findAll()).willReturn(authors);

        mvc.perform(get("/api/v1/author")).andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(authors)));
    }
}
