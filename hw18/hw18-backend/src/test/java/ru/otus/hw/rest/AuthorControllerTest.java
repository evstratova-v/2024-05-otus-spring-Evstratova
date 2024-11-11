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
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;

@DisplayName("REST контроллер по работе с авторами ")
@WebFluxTest(AuthorController.class)
@ContextConfiguration(classes = AuthorController.class)
public class AuthorControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AuthorRepository authorRepository;

    @DisplayName("должен возвращать всех авторов")
    @Test
    void shouldReturnAllAuthors() {
        List<Author> authors = Arrays.asList(
                new Author(new ObjectId().toHexString(), "Author_1"),
                new Author(new ObjectId().toHexString(), "Author_2")
        );
        Flux<Author> authorsFlux = Flux.fromIterable(authors);
        given(authorRepository.findAll()).willReturn(authorsFlux);

        List<AuthorDto> authorsDtoList = authors.stream().map(AuthorDto::toDto).toList();

        var webTestClientForTest = webTestClient.mutate()
                .responseTimeout(Duration.ofSeconds(5))
                .build();

        var result = webTestClientForTest
                .get().uri("/api/v1/author")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(AuthorDto.class)
                .getResponseBody();

        var step = StepVerifier.create(result);
        StepVerifier.Step<AuthorDto> stepResult = null;
        for (AuthorDto authorDto : authorsDtoList) {
            stepResult = step.expectNext(authorDto);
        }
        stepResult.verifyComplete();
    }
}
