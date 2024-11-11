package ru.otus.hw.repositories;

import reactor.core.publisher.Mono;
import ru.otus.hw.models.BookWithoutComments;

public interface BookRepositoryCustom {
    Mono<BookWithoutComments> saveIgnoreComments(BookWithoutComments bookWithoutComments);

    Mono<Void> deleteByIdWithComments(String id);
}
