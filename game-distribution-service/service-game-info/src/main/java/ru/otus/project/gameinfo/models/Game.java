package ru.otus.project.gameinfo.models;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "games")
@NamedEntityGraph(name = "game-developer-entity-graph", attributeNodes = {@NamedAttributeNode("developer")})
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "developer_id")
    private Developer developer;

    @Fetch(FetchMode.SUBSELECT)
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "games_genres", joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private List<Genre> genres;

    @Setter
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "os", column = @Column(name = "minimum_os")),
            @AttributeOverride(name = "cpu", column = @Column(name = "minimum_cpu")),
            @AttributeOverride(name = "ram", column = @Column(name = "minimum_ram")),
            @AttributeOverride(name = "gpu", column = @Column(name = "minimum_gpu")),
            @AttributeOverride(name = "storage", column = @Column(name = "minimum_storage"))
    })
    private SystemRequirement minimumSystemRequirement;

    @Setter
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "os", column = @Column(name = "recommended_os")),
            @AttributeOverride(name = "cpu", column = @Column(name = "recommended_cpu")),
            @AttributeOverride(name = "ram", column = @Column(name = "recommended_ram")),
            @AttributeOverride(name = "gpu", column = @Column(name = "recommended_gpu")),
            @AttributeOverride(name = "storage", column = @Column(name = "recommended_storage"))
    })
    private SystemRequirement recommendedSystemRequirement;

    public Game(long id, String title, String description, Developer developer, List<Genre> genres) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.developer = developer;
        this.genres = genres;
    }
}
