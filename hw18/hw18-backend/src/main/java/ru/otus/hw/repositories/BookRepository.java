package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookWithoutComments;

public interface BookRepository extends ReactiveMongoRepository<Book, String>, BookRepositoryCustom {
    Flux<BookWithoutComments> findAllWithoutCommentsBy();

    Mono<BookWithoutComments> findWithoutCommentsById(String id);
}
