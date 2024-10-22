package ru.otus.hw.integration.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannelSpec;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.PollerSpec;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.scheduling.PollerMetadata;
import ru.otus.hw.integration.domain.Caterpillar;
import ru.otus.hw.integration.domain.Chrysalis;
import ru.otus.hw.integration.services.MetamorphosisService;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Configuration
public class IntegrationConfig {

    private final AtomicInteger foodCounter = new AtomicInteger();

    @Bean
    public MessageChannelSpec<?, ?> caterpillarsChannel() {
        return MessageChannels.queue(10);
    }

    @Bean
    public MessageChannelSpec<?, ?> butterfliesChannel() {
        return MessageChannels.publishSubscribe();
    }

    @Bean(name = PollerMetadata.DEFAULT_POLLER)
    public PollerSpec poller() {
        return Pollers.fixedRate(100).maxMessagesPerPoll(2);
    }

    @Bean
    public IntegrationFlow caterpillarEatingSubFlow() {
        return sf -> sf
                .<Caterpillar, Caterpillar>transform(caterpillar -> {
                    int food = new Random().nextInt(1, 4);
                    caterpillar.eat(food);
                    log.info("Caterpillars ate {} amount of food", this.foodCounter.addAndGet(food));
                    return caterpillar;
                })
                .<Caterpillar>log(LoggingHandler.Level.INFO, "butterfly-flow.caterpillar-eat",
                        m -> "Caterpillar %s ate, new amount of nutrients: %s"
                                .formatted(m.getHeaders().getId(), m.getPayload().getNutrients()));
    }

    @Bean
    public IntegrationFlow wellFedCaterpillarSubFlow() {
        return sf -> sf
                .<Caterpillar>log(LoggingHandler.Level.INFO, "butterfly-flow.wel-fed-sub-flow",
                        m -> "Caterpillar %s is well-fed, nutrients %s. This is enough to be ready for pupation"
                                .formatted(m.getHeaders().getId(), m.getPayload().getNutrients()))
                .<Caterpillar, Caterpillar>transform(caterpillar -> {
                            caterpillar.setReadyForPupation(true);
                            return caterpillar;
                        }
                );
    }

    @Bean
    public IntegrationFlow hungryCaterpillarSubFlow() {
        return sf -> sf
                .<Caterpillar, Caterpillar>transform(caterpillar -> {
                            caterpillar.setReadyForPupation(new Random().nextBoolean());
                            return caterpillar;
                        }
                )
                .<Caterpillar>log(LoggingHandler.Level.INFO, "butterfly-flow.hungry-sub-flow",
                        m -> "Caterpillar %s is hungry, nutrients %s. Need some luck for pupation. Lucky: %s"
                                .formatted(m.getHeaders().getId(), m.getPayload().getNutrients(),
                                        m.getPayload().isReadyForPupation()));
    }

    @Bean
    public IntegrationFlow butterflyFlow(MetamorphosisService metamorphosisService) {
        return IntegrationFlow.from(caterpillarsChannel())
                .split()
                .gateway(caterpillarEatingSubFlow())
                .<Caterpillar, Boolean>route(caterpillar -> caterpillar.getNutrients() > 4, mapping -> mapping
                        .subFlowMapping(true, sf -> sf.gateway(wellFedCaterpillarSubFlow()))
                        .subFlowMapping(false, sf -> sf.gateway(hungryCaterpillarSubFlow())))

                .filter(Caterpillar::isReadyForPupation)
                .log(LoggingHandler.Level.INFO, "butterfly-flow.pupation",
                        m -> "Caterpillar %s is ready for pupation".formatted(m.getHeaders().getId()))
                .<Caterpillar, Chrysalis>transform(c -> new Chrysalis(c.getSpecies(), c.getNutrients()))

                .handle(metamorphosisService, "metamorphosis")
                .log(LoggingHandler.Level.INFO, "butterfly-flow.metamorphosis",
                        m -> "New butterfly %s was born".formatted(m.getHeaders().getId()))

                .aggregate(a -> a.sendPartialResultOnExpiry(true).groupTimeout(1))
                .channel(butterfliesChannel())
                .get();
    }
}
