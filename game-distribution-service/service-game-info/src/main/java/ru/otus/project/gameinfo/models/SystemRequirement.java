package ru.otus.project.gameinfo.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class SystemRequirement {

    @Column(name = "os")
    private String os;

    @Column(name = "cpu")
    private String cpu;

    @Column(name = "ram")
    private String ram;

    @Column(name = "gpu")
    private String gpu;

    @Column(name = "storage")
    private String storage;
}
