package ru.otus.project.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.otus.project.order.models.Order;

import java.time.LocalDateTime;
import java.time.ZoneId;

@AllArgsConstructor
@Data
public class OrderDto {

    @Min(1)
    private long id;

    @Min(1)
    private long gameId;

    @Min(1)
    private long userId;

    @Min(100)
    private long cost;

    @NotNull
    private LocalDateTime created;

    private boolean paid;

    public static OrderDto toDto(Order order) {
        return new OrderDto(order.getId(),
                order.getGameProduct().getGameId(),
                order.getUserId(),
                order.getCost(),
                order.getCreated().atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime(),
                order.isPaid());
    }
}
