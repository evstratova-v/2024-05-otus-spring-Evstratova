package ru.otus.hw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class Application {
    // --spring.shell.interactive.enabled=false --spring.batch.job.enabled=true
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
