package ru.otus.project.order.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;

import java.time.OffsetDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "game_orders")
@NamedEntityGraph(name = "orders-products-entity-graph", attributeNodes = {@NamedAttributeNode("gameProduct")})
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private GameProduct gameProduct;

    @CreatedBy
    @Column(name = "user_id")
    private long userId;

    @Column(name = "cost")
    private long cost;

    @CreationTimestamp
    @Column(name = "created")
    private OffsetDateTime created;

    @Column(name = "paid")
    private boolean paid;

    public Order(GameProduct gameProduct, long userId, boolean paid) {
        this.gameProduct = gameProduct;
        this.userId = userId;
        this.cost = gameProduct.getPrice();
        this.paid = paid;
    }
}
