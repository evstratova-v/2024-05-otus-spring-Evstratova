package ru.otus.project.order.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "game_products")
public class GameProduct {
    @Id
    @Column(name = "game_id")
    private long gameId;

    @Setter
    @Column(name = "price")
    private long price;

    @CreatedBy
    @Column(name = "created_by")
    private long developerId;

    public GameProduct(long gameId, long price) {
        this.gameId = gameId;
        this.price = price;
    }
}
