package ru.otus.hw.batch.processor;

import org.bson.types.ObjectId;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.jpa.Author;
import ru.otus.hw.models.mongo.MongoAuthor;

@Component
public class AuthorProcessor implements ItemProcessor<Author, MongoAuthor> {

    @Cacheable(value = "jpaId_mongoAuthor", key = "#author.getId()")
    @Override
    public MongoAuthor process(Author author) {
        return new MongoAuthor(new ObjectId().toHexString(), author.getFullName());
    }
}
