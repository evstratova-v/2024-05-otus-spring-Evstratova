package ru.otus.hw.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

@RestController
@RequiredArgsConstructor
public class GenreController {

    private final GenreRepository genreRepository;

    @GetMapping("/api/v1/genre")
    public Flux<Genre> getAllGenres() {
        return genreRepository.findAll();
    }
}
