package ru.otus.hw.integration.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.integration.domain.Butterfly;
import ru.otus.hw.integration.domain.Caterpillar;
import ru.otus.hw.integration.domain.Species;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ButterflyLifecycleServiceImpl implements ButterflyLifecycleService {

    private final ButterflyLifecycleGateway butterflyLifecycleGateway;

    @Override
    public void startGenerateButterfliesLoop() {
        ForkJoinPool pool = ForkJoinPool.commonPool();
        for (int i = 0; i < 10; i++) {
            int num = i + 1;
            pool.execute(() -> {
                Species species = getRandomButterflySpecies();
                Collection<Caterpillar> caterpillars = generateCaterpillars(species);
                log.info("{}, Start process caterpillars, species: {}, amount: {}, nutrients: {}",
                        num, species.getName(), caterpillars.size(), caterpillars.stream()
                                .map(c -> String.valueOf(c.getNutrients())).collect(Collectors.joining(",")));

                Collection<Butterfly> butterflies = butterflyLifecycleGateway.process(caterpillars);

                if (butterflies == null) {
                    log.info("{}, End process, not a single one butterfly grew from {} caterpillar, species: {}",
                            num, caterpillars.size(), species.getName());
                } else {
                    log.info("{}, End process, {} butterflies grew from {} caterpillars, species: {}, wingspan: {}",
                            num, butterflies.size(), caterpillars.size(), species.getName(), butterflies.stream()
                                    .map(b -> String.valueOf(b.wingSpan())).collect(Collectors.joining(",")));
                }
            });
            delay();
        }
    }

    private static Species getRandomButterflySpecies() {
        Species[] species = Species.values();
        int length = species.length;
        int numOfRandomSpecies = new Random().nextInt(length);
        return species[numOfRandomSpecies];
    }

    private static Collection<Caterpillar> generateCaterpillars(Species species) {
        List<Caterpillar> caterpillars = new ArrayList<>();
        for (int i = 0; i < new Random().nextInt(1, 5); i++) {
            Caterpillar caterpillar = new Caterpillar(species, new Random().nextInt(1, 3));
            caterpillars.add(caterpillar);
        }
        return caterpillars;
    }

    private void delay() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
