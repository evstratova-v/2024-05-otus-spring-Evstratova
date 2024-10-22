package ru.otus.hw.integration.services;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.otus.hw.integration.domain.Butterfly;
import ru.otus.hw.integration.domain.Caterpillar;

import java.util.Collection;

@MessagingGateway
public interface ButterflyLifecycleGateway {

    @Gateway(requestChannel = "caterpillarsChannel", replyChannel = "butterfliesChannel")
    Collection<Butterfly> process(Collection<Caterpillar> caterpillars);
}
