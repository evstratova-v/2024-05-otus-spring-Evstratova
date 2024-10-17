package ru.otus.hw.batch.processor;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.jpa.Book;
import ru.otus.hw.models.mongo.MongoAuthor;
import ru.otus.hw.models.mongo.MongoBook;
import ru.otus.hw.models.mongo.MongoComment;
import ru.otus.hw.models.mongo.MongoGenre;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class BookProcessor implements ItemProcessor<Book, MongoBook> {

    private final CacheManager cacheManager;

    @Override
    public MongoBook process(Book book) {
        MongoAuthor mongoAuthor = getMongoAuthorFromCache(book);
        List<MongoGenre> mongoGenres = getMongoGenresFromCache(book);

        return getMongoBookWithCommentsFromCache(book, mongoAuthor, mongoGenres);
    }

    private MongoAuthor getMongoAuthorFromCache(Book book) {
        return cacheManager.getCache("jpaId_mongoAuthor")
                .get(book.getAuthor().getId(), MongoAuthor.class);
    }

    private List<MongoGenre> getMongoGenresFromCache(Book book) {
        Cache cache = cacheManager.getCache("jpaId_mongoGenre");
        return book.getGenres().stream()
                .map(genre -> cache.get(genre.getId(), MongoGenre.class)).toList();
    }

    private MongoBook getMongoBookWithCommentsFromCache(Book book, MongoAuthor mongoAuthor,
                                                        List<MongoGenre> mongoGenres) {
        String mongoBookId;
        List<MongoComment> mongoComments;
        Cache jpaBookIdMongoBookIdCache = cacheManager.getCache("jpaBookId_mongoBookId");

        if (jpaBookIdMongoBookIdCache.get(book.getId()) == null) {
            mongoBookId = new ObjectId().toHexString();
            mongoComments = new ArrayList<>();
        } else {
            Cache mongoBookIdCommentsCache = cacheManager.getCache("mongoBookId_mongoComments");

            mongoBookId = jpaBookIdMongoBookIdCache.get(book.getId(), String.class);
            mongoComments = mongoBookIdCommentsCache.get(mongoBookId, List.class);
        }

        return new MongoBook(mongoBookId, book.getTitle(), mongoAuthor, mongoGenres, mongoComments);
    }
}
