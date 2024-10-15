package ru.otus.hw.batch.processor;

import org.bson.types.ObjectId;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.jpa.Genre;
import ru.otus.hw.models.mongo.MongoGenre;

@Component
public class GenreProcessor implements ItemProcessor<Genre, MongoGenre> {

    @Cacheable(value = "jpaId_mongoGenre", key = "#genre.getId()")
    @Override
    public MongoGenre process(Genre genre) {
        return new MongoGenre(new ObjectId().toHexString(), genre.getName());
    }
}
