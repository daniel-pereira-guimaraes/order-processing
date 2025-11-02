package com.danielpgbrasil.orderprocessing.infrastructure.controller;

import com.danielpgbrasil.orderprocessing.application.order.GetOrderService;
import com.danielpgbrasil.orderprocessing.domain.order.Order;
import com.danielpgbrasil.orderprocessing.domain.order.OrderId;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEvent;
import com.danielpgbrasil.orderprocessing.infrastructure.config.swagger.BadRequestResponse;
import com.danielpgbrasil.orderprocessing.infrastructure.shared.AppErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "Pedidos")
@RestController
@RequestMapping("/orders")
public class GetOrderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetOrderController.class);

    private final GetOrderService getOrderService;

    public GetOrderController(GetOrderService getOrderService) {
        this.getOrderService = getOrderService;
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtém um pedido pelo ID",
            description = "Retorna os dados completos de um pedido. Pode incluir também seus eventos, caso solicitado.",
            parameters = {
                    @Parameter(name = "includeEvents", description = "Define se os eventos do pedido devem ser incluídos na resposta", example = "true")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Pedido obtido com sucesso",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Response.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Pedido com eventos incluídos",
                                                    value = """
                                                    {
                                                      "order": {
                                                        "id": 1,
                                                        "customerName": "Joao Silva",
                                                        "customerAddress": "Rua A, 123",
                                                        "items": [
                                                          {"productId": 1, "quantity": 2, "price": 50.0},
                                                          {"productId": 2, "quantity": 1, "price": 30.0}
                                                        ],
                                                        "status": "CREATED"
                                                      },
                                                      "events": [
                                                        {"id": 10, "type": "CREATED", "createdAt": 1730000000000, "published": true},
                                                        {"id": 11, "type": "DELIVERED", "createdAt": 1730000500000, "published": false}
                                                      ]
                                                    }
                                                    """
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Requisição inválida",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AppErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Pedido não encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AppErrorResponse.class)
                            )
                    )
            }
    )
    @BadRequestResponse
    public ResponseEntity<Response> get(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean includeEvents) {

        LOGGER.info("Buscando pedido com ID: {} (includeEvents={})", id, includeEvents);

        var result = getOrderService.getOrder(OrderId.of(id), includeEvents);
        return ResponseEntity.ok(Response.of(result));
    }

    @Schema(name = "GetOrderResponse")
    public record Response(
            @Schema(description = "Dados do pedido")
            OrderResponse order,
            @Schema(description = "Eventos associados ao pedido (opcional)")
            List<EventResponse> events
    ) {
        public static Response of(GetOrderService.Response response) {
            return new Response(
                    OrderResponse.of(response.order()),
                    response.events().stream().map(EventResponse::of).toList()
            );
        }
    }

    @Schema(name = "GetOrderOrderResponse")
    public record OrderResponse(
            Long id,
            String customerName,
            String customerAddress,
            List<ItemResponse> items,
            String status
    ) {
        public static OrderResponse of(Order order) {
            return new OrderResponse(
                    order.id().value(),
                    order.details().customerName(),
                    order.details().customerAddress(),
                    order.details().items().stream()
                            .map(i -> new ItemResponse(i.productId(), i.quantity(), i.price()))
                            .toList(),
                    order.status().name()
            );
        }
    }

    @Schema(name = "GetOrderItemResponse")
    public record ItemResponse(
            Long productId,
            Integer quantity,
            BigDecimal price
    ) {}

    @Schema(name = "GetOrderEventResponse")
    public record EventResponse(
            Long id,
            String type,
            long createdAt,
            boolean published
    ) {
        public static EventResponse of(OrderEvent event) {
            return new EventResponse(
                    event.id().value(),
                    event.type().name(),
                    event.createdAt().value(),
                    event.isPublished()
            );
        }
    }
}
