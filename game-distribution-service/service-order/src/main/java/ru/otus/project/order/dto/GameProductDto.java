package ru.otus.project.order.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.otus.project.order.models.GameProduct;

@Data
@AllArgsConstructor
public class GameProductDto {

    @Min(1)
    private long gameId;

    @Min(100)
    private long price;

    public static GameProductDto toDto(GameProduct gameProduct) {
        return new GameProductDto(gameProduct.getGameId(), gameProduct.getPrice());
    }
}
