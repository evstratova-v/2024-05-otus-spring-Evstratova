package ru.otus.hw.batch.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.models.mongo.MongoAuthor;
import ru.otus.hw.models.mongo.MongoBook;
import ru.otus.hw.models.mongo.MongoComment;
import ru.otus.hw.models.mongo.MongoGenre;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.otus.hw.batch.config.JobConfig.IMPORT_LIBRARY_JOB_NAME;

@SpringBootTest
@SpringBatchTest
public class ImportLibraryJobTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void clearMetaData() {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    void testJob() throws Exception {
        Job job = jobLauncherTestUtils.getJob();
        assertThat(job).isNotNull()
                .extracting(Job::getName)
                .isEqualTo(IMPORT_LIBRARY_JOB_NAME);

        JobParameters parameters = new JobParametersBuilder().toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(parameters);
        assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");

        assertThat(mongoTemplate.findAll(MongoGenre.class)).matches(l -> l.size() == 6);
        assertThat(mongoTemplate.findAll(MongoAuthor.class)).matches(l -> l.size() == 3);
        assertThat(mongoTemplate.findAll(MongoComment.class)).matches(l -> l.size() == 6);
        assertThat(mongoTemplate.findAll(MongoBook.class)).matches(l -> l.size() == 4);
    }
}
