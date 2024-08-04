package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.BookWithoutComments;

public interface BookWithoutCommentsRepository extends MongoRepository<BookWithoutComments, String> {
}
