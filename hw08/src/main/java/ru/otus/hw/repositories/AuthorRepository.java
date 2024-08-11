package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.Author;

public interface AuthorRepository extends MongoRepository<Author, String> {
}
