package ru.otus.hw.shell;


import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.Properties;

import static ru.otus.hw.batch.config.JobConfig.IMPORT_LIBRARY_JOB_NAME;

@RequiredArgsConstructor
@ShellComponent
public class BatchCommands {

    private final Job importLibraryJob;

    private final JobLauncher jobLauncher;

    private final JobOperator jobOperator;

    private final JobExplorer jobExplorer;

    private final JobRepository jobRepository;

    @SuppressWarnings("unused")
    @ShellMethod(value = "startMigrationJobWithJobLauncher", key = "sm-jl")
    public void startMigrationJobWithJobLauncher() throws Exception {
        JobExecution execution = jobLauncher.run(importLibraryJob, new JobParametersBuilder().toJobParameters());
        System.out.println(execution);
    }

    @SuppressWarnings("unused")
    @ShellMethod(value = "startMigrationJobWithJobOperator", key = "sm-jo")
    public void startMigrationJobWithJobOperator() throws Exception {
        Long executionId = jobOperator.start(IMPORT_LIBRARY_JOB_NAME, new Properties());
        System.out.println(jobOperator.getSummary(executionId));
    }

    @SuppressWarnings("unused")
    @ShellMethod(value = "showInfo", key = "i")
    public void showInfo() {
        System.out.println(jobExplorer.getJobNames());
        System.out.println(jobExplorer.getLastJobInstance(IMPORT_LIBRARY_JOB_NAME));
    }

    @SuppressWarnings("unused")
    @ShellMethod(value = "restart", key = "r")
    public void restartJob() throws Exception {
        JobExecution jobExecution = jobRepository.getLastJobExecution(IMPORT_LIBRARY_JOB_NAME,
                new JobParametersBuilder().toJobParameters());

        if (jobExecution == null) {
            System.out.println("Работа не была запущена");
        } else {
            Long executionId = jobExecution.getId();
            Long newExecutionId = jobOperator.restart(executionId);

            System.out.println(jobOperator.getSummary(newExecutionId));
        }
    }
}
