package ru.otus.project.order.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.project.order.dto.OrderDto;
import ru.otus.project.order.exceptions.EntityNotFoundException;
import ru.otus.project.order.exceptions.OrderAlreadyExistsException;
import ru.otus.project.order.services.OrderService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Get orders by user id", description = "Returns list of OrderDto",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/api/v1/order")
    public List<OrderDto> getOrderByUserId(@AuthenticationPrincipal Jwt principal) {
        long userId = Long.parseLong(principal.getClaims().get("userId").toString());
        return orderService.findAllOrdersByUserId(userId);
    }

    @Operation(summary = "Get orders by user id and game id", description = "Returns OrderDto",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/api/v1/order/{game_id}")
    public OrderDto getOrderByUserIdAndGameId(@AuthenticationPrincipal Jwt principal,
                                              @RequestParam(name = "game_id") Long gameId) {
        long userId = Long.parseLong(principal.getClaims().get("userId").toString());

        return orderService.findOrderByUserIdAndGameId(userId, gameId)
                .orElseThrow(() -> new EntityNotFoundException("Order for game_id %s and user_id %s not found"
                        .formatted(gameId, userId)));
    }

    @Operation(summary = "Create order by user_id and game_id", description = "Returns OrderDto",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/api/v1/order")
    public OrderDto addOrderByGameId(@AuthenticationPrincipal Jwt principal, @RequestParam("game_id") long gameId) {
        long userId = Long.parseLong(principal.getClaims().get("userId").toString());
        return orderService.insert(userId, gameId);
    }

    @ExceptionHandler({OrderAlreadyExistsException.class, EntityNotFoundException.class})
    public ResponseEntity<String> handleException(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
