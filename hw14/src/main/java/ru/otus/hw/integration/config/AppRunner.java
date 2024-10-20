package ru.otus.hw.integration.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.otus.hw.integration.services.ButterflyLifecycleService;

@RequiredArgsConstructor
@Component
public class AppRunner implements CommandLineRunner {

    private final ButterflyLifecycleService butterflyLifecycleService;

    @Override
    public void run(String... args) {
        butterflyLifecycleService.startGenerateButterfliesLoop();
    }
}
