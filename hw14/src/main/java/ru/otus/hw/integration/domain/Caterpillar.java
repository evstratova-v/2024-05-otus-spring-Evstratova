package ru.otus.hw.integration.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Caterpillar {
    private final Species species;

    private int nutrients;

    @Setter
    private boolean readyForPupation;

    public Caterpillar(Species species, int nutrients) {
        this.species = species;
        this.nutrients = nutrients;
        this.readyForPupation = false;
    }

    public void eat(int food) {
        this.nutrients = this.getNutrients() + food;
    }
}
