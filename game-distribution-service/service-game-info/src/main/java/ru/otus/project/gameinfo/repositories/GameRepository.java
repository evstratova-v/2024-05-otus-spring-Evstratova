package ru.otus.project.gameinfo.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import ru.otus.project.gameinfo.models.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.project.gameinfo.projections.ShortGameProjection;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {

    @EntityGraph(value = "game-developer-entity-graph")
    Optional<Game> findById(long id);

    @EntityGraph(value = "game-developer-entity-graph")
    List<ShortGameProjection> findAllProjectedBy();

    @EntityGraph(value = "game-developer-entity-graph")
    List<ShortGameProjection> findAllByGenresIdIn(List<Long> genresIds);

    @EntityGraph(value = "game-developer-entity-graph")
    List<ShortGameProjection> findAllByTitleContaining(String pattern);

    @EntityGraph(value = "game-developer-entity-graph")
    List<ShortGameProjection> findAllByTitleContainingAndGenresIdIn(String pattern, List<Long> genresIds);
}
