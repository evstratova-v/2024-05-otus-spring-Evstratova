package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Контроллер для редиректа ")
@WebMvcTest(RedirectController.class)
public class RedirectControllerTest {

    @Autowired
    private MockMvc mvc;

    @DisplayName("должен направлять любые запросы на главную страницу")
    @ParameterizedTest
    @ValueSource(strings = {"/test", "/add", "/edit/1"})
    void shouldRedirect(String urlTemplate) throws Exception {
        mvc.perform(get(urlTemplate)).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}
