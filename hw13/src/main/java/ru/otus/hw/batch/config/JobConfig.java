package ru.otus.hw.batch.config;

import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.batch.processor.AuthorProcessor;
import ru.otus.hw.batch.processor.BookProcessor;
import ru.otus.hw.batch.processor.CommentProcessor;
import ru.otus.hw.batch.processor.GenreProcessor;
import ru.otus.hw.models.jpa.Author;
import ru.otus.hw.models.jpa.Book;
import ru.otus.hw.models.jpa.Comment;
import ru.otus.hw.models.jpa.Genre;
import ru.otus.hw.models.mongo.MongoAuthor;
import ru.otus.hw.models.mongo.MongoBook;
import ru.otus.hw.models.mongo.MongoComment;
import ru.otus.hw.models.mongo.MongoGenre;


@SuppressWarnings("unused")
@Configuration
public class JobConfig {
    public static final String IMPORT_LIBRARY_JOB_NAME = "importLibraryJob";

    private static final int CHUNK_SIZE = 5;

    private final Logger logger = LoggerFactory.getLogger("Batch");

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Bean
    public JpaPagingItemReader<Author> authorReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<Author>()
                .name("authorReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select a from Author a")
                .build();
    }

    @Bean
    public JpaPagingItemReader<Genre> genreReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<Genre>()
                .name("genreReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select g from Genre g")
                .build();
    }

    @Bean
    public JpaPagingItemReader<Comment> commentReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<Comment>()
                .name("commentReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select c from Comment c")
                .build();
    }

    @Bean
    public JpaPagingItemReader<Book> bookReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<Book>()
                .name("bookReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select b from Book b")
                .build();
    }

    @Bean
    public MongoItemWriter<MongoAuthor> authorWriter(MongoTemplate mongoTemplate) {
        return new MongoItemWriterBuilder<MongoAuthor>()
                .template(mongoTemplate)
                .collection("authors")
                .build();
    }

    @Bean
    public MongoItemWriter<MongoGenre> genreWriter(MongoTemplate mongoTemplate) {
        return new MongoItemWriterBuilder<MongoGenre>()
                .template(mongoTemplate)
                .collection("genres")
                .build();
    }

    @Bean
    public MongoItemWriter<MongoComment> commentWriter(MongoTemplate mongoTemplate) {
        return new MongoItemWriterBuilder<MongoComment>()
                .template(mongoTemplate)
                .collection("comments")
                .build();
    }

    @Bean
    public MongoItemWriter<MongoBook> bookWriter(MongoTemplate mongoTemplate) {
        return new MongoItemWriterBuilder<MongoBook>()
                .template(mongoTemplate)
                .collection("books")
                .build();
    }

    @Bean
    public Job migrationSqlToMongoJob(Flow splitAuthorGenreFlow, Step transformBookStep, Step transformCommentStep) {
        return new JobBuilder(IMPORT_LIBRARY_JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(splitAuthorGenreFlow)
                .next(transformCommentStep)
                .next(transformBookStep)
                .end()
                .build();
    }

    @Bean
    public Flow splitAuthorGenreFlow(Step transformAuthorStep, Step transformGenreStep) {
        return new FlowBuilder<SimpleFlow>("splitAuthorGenreFlow")
                .split(taskExecutor())
                .add(flowAuthor(transformAuthorStep), flowGenre(transformGenreStep))
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor("spring_batch");
    }

    @Bean
    public Flow flowAuthor(Step transformAuthorStep) {
        return new FlowBuilder<SimpleFlow>("flowAuthor")
                .start(transformAuthorStep)
                .build();
    }

    @Bean
    public Flow flowGenre(Step transformGenreStep) {
        return new FlowBuilder<SimpleFlow>("flowGenre")
                .start(transformGenreStep)
                .build();
    }

    @Bean
    public Step transformAuthorStep(JpaPagingItemReader<Author> reader, MongoItemWriter<MongoAuthor> writer,
                                    AuthorProcessor itemProcessor) {
        return new StepBuilder("transformAuthorStep", jobRepository)
                .<Author, MongoAuthor>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(reader)
                .processor(itemProcessor)
                .writer(writer)
                .build();
    }

    @Bean
    public Step transformGenreStep(JpaPagingItemReader<Genre> reader, MongoItemWriter<MongoGenre> writer,
                                   GenreProcessor itemProcessor) {
        return new StepBuilder("transformGenreStep", jobRepository)
                .<Genre, MongoGenre>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(reader)
                .processor(itemProcessor)
                .writer(writer)
                .build();
    }

    @Bean
    public Step transformCommentStep(JpaPagingItemReader<Comment> reader, MongoItemWriter<MongoComment> writer,
                                     CommentProcessor itemProcessor) {
        return new StepBuilder("transformCommentStep", jobRepository)
                .<Comment, MongoComment>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(reader)
                .processor(itemProcessor)
                .writer(writer)
                .build();
    }

    @Bean
    public Step transformBookStep(JpaPagingItemReader<Book> reader, MongoItemWriter<MongoBook> writer,
                                  BookProcessor itemProcessor) {
        return new StepBuilder("transformBookStep", jobRepository)
                .<Book, MongoBook>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(reader)
                .processor(itemProcessor)
                .writer(writer)
                .build();
    }
}
