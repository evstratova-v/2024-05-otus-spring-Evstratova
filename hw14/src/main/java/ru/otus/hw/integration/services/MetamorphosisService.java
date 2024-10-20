package ru.otus.hw.integration.services;

import ru.otus.hw.integration.domain.Butterfly;
import ru.otus.hw.integration.domain.Chrysalis;

public interface MetamorphosisService {
    Butterfly metamorphosis(Chrysalis chrysalis);
}
