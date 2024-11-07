package ru.otus.hw.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    private final CacheManager cacheManager;

    @CircuitBreaker(name = "authorServiceCircuitBreaker", fallbackMethod = "findAllInCache")
    @Override
    public List<AuthorDto> findAll() {
        List<AuthorDto> authors = authorRepository.findAll().stream().map(AuthorDto::toDto).toList();
        authors.forEach(author -> cacheManager.getCache("authors").put(author.getId(), author));
        return authors;
    }

    @SuppressWarnings("unchecked")
    public List<AuthorDto> findAllInCache(Throwable t) {
        log.error("error while finding all authors: {}", t.getMessage());
        Cache cache = cacheManager.getCache("authors");
        ConcurrentHashMap<Long, AuthorDto> authors = ((ConcurrentHashMap<Long, AuthorDto>) cache.getNativeCache());
        return authors.values().stream().toList();
    }
}
