package ru.otus.hw.rest;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

import java.time.Duration;
import java.util.List;

import static org.mockito.BDDMockito.given;

@DisplayName("REST контроллер по работе с жанрами ")
@WebFluxTest(GenreController.class)
@ContextConfiguration(classes = GenreController.class)
public class GenreControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private GenreRepository genreRepository;

    @DisplayName("должен возвращать все жанры")
    @Test
    void shouldReturnAllGenres() {
        List<Genre> genres = List.of(
                new Genre(new ObjectId().toHexString(), "Genre_1"),
                new Genre(new ObjectId().toHexString(), "Genre_2")
        );
        List<GenreDto> genresDto = genres.stream().map(GenreDto::toDto).toList();

        Flux<Genre> genresFlux = Flux.fromIterable(genres);
        given(genreRepository.findAll()).willReturn(genresFlux);

        var webTestClientForTest = webTestClient.mutate()
                .responseTimeout(Duration.ofSeconds(5))
                .build();

        var result = webTestClientForTest
                .get().uri("/api/v1/genre")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(GenreDto.class)
                .getResponseBody();

        var step = StepVerifier.create(result);
        StepVerifier.Step<GenreDto> stepResult = null;
        for (GenreDto genreDto : genresDto) {
            stepResult = step.expectNext(genreDto);
        }
        stepResult.verifyComplete();
    }
}
