package ru.otus.hw.mongock.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.Arrays;
import java.util.List;

@ChangeLog
public class DatabaseChangelog {

    private List<Author> authors;

    private List<Genre> genres;

    private List<Book> books;

    @ChangeSet(order = "001", id = "dropDb", author = "evstratova-v", runAlways = true)
    public void dropDb(MongoDatabase db) {
        db.drop();
    }

    @ChangeSet(order = "002", id = "insertAuthors", author = "evstratova-v")
    public void insertAuthors(AuthorRepository authorRepository) {
        authors = Arrays.asList(new Author("Author_1"),
                new Author("Author_2"),
                new Author("Author_3"));
        authorRepository.saveAll(authors);
    }

    @ChangeSet(order = "003", id = "insertGenres", author = "evstratova-v")
    public void insertGenres(GenreRepository genreRepository) {
        genres = Arrays.asList(new Genre("Genre_1"),
                new Genre("Genre_2"),
                new Genre("Genre_3"),
                new Genre("Genre_4"),
                new Genre("Genre_5"),
                new Genre("Genre_6")
        );
        genreRepository.saveAll(genres);
    }

    @ChangeSet(order = "004", id = "insertBooks", author = "evstratova-v")
    public void insertBooks(BookRepository bookRepository) {
        books = Arrays.asList(
                new Book("BookTitle_1", authors.get(0), Arrays.asList(genres.get(0), genres.get(1))),
                new Book("BookTitle_2", authors.get(1), Arrays.asList(genres.get(2), genres.get(3))),
                new Book("BookTitle_3", authors.get(2), Arrays.asList(genres.get(4), genres.get(5)))
        );
        books = bookRepository.saveAll(books);
    }

    @ChangeSet(order = "005", id = "insertComments", author = "evstratova-v")
    public void insertComments(CommentRepository commentRepository, BookRepository bookRepository) {
        List<Comment> comments = Arrays.asList(
                new Comment("CommentText_1", books.get(0)),
                new Comment("CommentText_2", books.get(0)),
                new Comment("CommentText_3", books.get(1)),
                new Comment("CommentText_4", books.get(1)),
                new Comment("CommentText_5", books.get(2)),
                new Comment("CommentText_6", books.get(2))
        );
        commentRepository.saveAll(comments);

        books.get(0).setComments(Arrays.asList(comments.get(0), comments.get(1)));
        books.get(1).setComments(Arrays.asList(comments.get(2), comments.get(3)));
        books.get(2).setComments(Arrays.asList(comments.get(4), comments.get(5)));
        bookRepository.saveAll(books);
    }
}
