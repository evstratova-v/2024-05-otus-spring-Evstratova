package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import ru.otus.hw.models.Author;

@RepositoryRestResource(path = "author")
public interface AuthorRepository extends JpaRepository<Author, Long> {

    @RestResource(path = "fullNames", rel = "fullNames")
    Author findByFullName(String fullName);
}
