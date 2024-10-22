package ru.otus.hw.integration.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.integration.domain.Butterfly;
import ru.otus.hw.integration.domain.Chrysalis;

import java.util.Random;

@Slf4j
@Service
public class MetamorphosisServiceImpl implements MetamorphosisService {

    @Override
    public Butterfly metamorphosis(Chrysalis chrysalis) {
        log.info("Metamorphosis {} start", chrysalis.species().getName());
        delay();
        log.info("Metamorphosis {} done", chrysalis.species().getName());
        return new Butterfly(chrysalis.species(), new Random().nextInt(45, 65));
    }

    private static void delay() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
