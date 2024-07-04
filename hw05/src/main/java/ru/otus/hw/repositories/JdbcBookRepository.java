package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private final GenreRepository genreRepository;

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Override
    public Optional<Book> findById(long id) {
        Book book = namedParameterJdbcOperations.query(
                "select books.id, books.title, authors.id, authors.full_name, genres.id, genres.name from " +
                        "books inner join authors on books.author_id = authors.id " +
                        "inner join books_genres on books.id = books_genres.book_id " +
                        "inner join genres on genres.id = books_genres.genre_id " +
                        "where books.id = :id", Map.of("id", id), new BookResultSetExtractor());
        return Optional.ofNullable(book);
    }

    @Override
    public List<Book> findAll() {
        var genres = genreRepository.findAll();
        var relations = getAllGenreRelations();
        var books = getAllBooksWithoutGenres();
        mergeBooksInfo(books, genres, relations);
        return books;
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        namedParameterJdbcOperations.update("delete from books where id = :id", Map.of("id", id));
    }

    private List<Book> getAllBooksWithoutGenres() {
        return namedParameterJdbcOperations.query("select books.id, books.title, authors.id, authors.full_name " +
                "from books inner join authors on books.author_id = authors.id", new BookRowMapper());
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        return namedParameterJdbcOperations.query("select book_id, genre_id from books_genres",
                (rs, i) -> new BookGenreRelation(rs.getLong("book_id"), rs.getLong("genre_id")));
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {
        for (var book : booksWithoutGenres) {
            var genreIdsForBook = relations.stream().filter(relation -> relation.bookId() == book.getId())
                    .map(BookGenreRelation::genreId).toList();
            var genresForBook = genres.stream().filter(genre -> genreIdsForBook.contains(genre.getId())).toList();
            book.setGenres(genresForBook);
        }
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource(Map.of(
                "title", book.getTitle(),
                "author_id", book.getAuthor().getId()
        ));

        namedParameterJdbcOperations.update("insert into books(title, author_id) values(:title, :author_id)",
                sqlParameterSource, keyHolder);

        //noinspection DataFlowIssue
        book.setId(keyHolder.getKeyAs(Long.class));
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        int updatedCount = namedParameterJdbcOperations.update(
                "update books set title = :title, author_id = :author_id where id = :id", Map.of(
                        "title", book.getTitle(),
                        "author_id", book.getAuthor().getId(),
                        "id", book.getId()));
        if (updatedCount == 0) {
            throw new EntityNotFoundException("Book with id %s not found".formatted(book.getId()));
        }

        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        var genres = book.getGenres();
        long bookId = book.getId();

        MapSqlParameterSource[] batch = genres.stream().map(
                genre -> new MapSqlParameterSource(Map.of(
                        "book_id", bookId,
                        "genre_id", genre.getId()
                ))).toArray(MapSqlParameterSource[]::new);

        namedParameterJdbcOperations.batchUpdate(
                "insert into books_genres(book_id, genre_id) values (:book_id, :genre_id)", batch);
    }

    private void removeGenresRelationsFor(Book book) {
        namedParameterJdbcOperations.update(
                "delete from books_genres where book_id = :book_id", Map.of("book_id", book.getId()));
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            long id = rs.getLong("books.id");
            String title = rs.getString("books.title");
            long authorId = rs.getLong("authors.id");
            String authorFullName = rs.getString("authors.full_name");
            return new Book(id, title, new Author(authorId, authorFullName), new ArrayList<>());
        }
    }

    @SuppressWarnings("ClassCanBeRecord")
    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

        @Override
        public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
            Book book = null;
            while (rs.next()) {
                if (book == null) {
                    book = new Book();
                    book.setId(rs.getLong("books.id"));
                    book.setTitle(rs.getString("books.title"));
                    book.setAuthor(new Author(rs.getLong("authors.id"),
                            rs.getString("authors.full_name")));
                    book.setGenres(new ArrayList<>());
                }
                Genre genre = new Genre(rs.getLong("genres.id"), rs.getString("genres.name"));
                book.getGenres().add(genre);
            }
            return book;
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }
}
