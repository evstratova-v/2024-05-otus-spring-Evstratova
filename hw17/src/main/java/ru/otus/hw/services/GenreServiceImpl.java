package ru.otus.hw.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    private final CacheManager cacheManager;

    @CircuitBreaker(name = "genreServiceCircuitBreaker", fallbackMethod = "findAllInCache")
    @Override
    public List<GenreDto> findAll() {
        List<GenreDto> genres = genreRepository.findAll().stream().map(GenreDto::toDto).toList();
        genres.forEach(genre -> cacheManager.getCache("genres").put(genre.getId(), genre));
        return genres;
    }

    @SuppressWarnings("unchecked")
    public List<GenreDto> findAllInCache(Throwable t) {
        log.error("error while finding all genres: {}", t.getMessage());
        Cache cache = cacheManager.getCache("genres");
        ConcurrentHashMap<Long, GenreDto> genres = ((ConcurrentHashMap<Long, GenreDto>) cache.getNativeCache());
        return genres.values().stream().toList();
    }
}
