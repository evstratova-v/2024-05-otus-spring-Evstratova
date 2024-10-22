package ru.otus.hw.integration.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Species {
    VANESSA_ATALANTA("Vanessa atalanta"),
    PAPILIO_MACHAON("Papilio machaon"),
    GONEPTERYX_RHAMNI("Gonepteryx rhamni"),
    APATURA_IRIS("Apatura iris"),
    PARNASSIUS_APOLLO("Parnassius apollo");

    private final String name;
}
