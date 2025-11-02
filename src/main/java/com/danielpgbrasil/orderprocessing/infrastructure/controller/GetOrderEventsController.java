package com.danielpgbrasil.orderprocessing.infrastructure.controller;

import com.danielpgbrasil.orderprocessing.application.order.event.GetOrderEventsService;
import com.danielpgbrasil.orderprocessing.domain.order.OrderId;
import com.danielpgbrasil.orderprocessing.domain.order.event.OrderEvent;
import com.danielpgbrasil.orderprocessing.infrastructure.config.swagger.BadRequestResponse;
import com.danielpgbrasil.orderprocessing.infrastructure.shared.AppErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Pedidos")
@RestController
@RequestMapping("/orders")
public class GetOrderEventsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetOrderEventsController.class);

    private final GetOrderEventsService getOrderEventsService;

    public GetOrderEventsController(GetOrderEventsService getOrderEventsService) {
        this.getOrderEventsService = getOrderEventsService;
    }

    @GetMapping("/{id}/events")
    @Operation(
            summary = "Obtém os eventos de um pedido",
            description = "Retorna todos os eventos associados ao pedido informado.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Eventos obtidos com sucesso",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = EventResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Eventos de exemplo",
                                                    value = """
                                                    [
                                                      {"id": 10, "type": "CREATED", "createdAt": 1730000000000, "published": true},
                                                      {"id": 11, "type": "DELIVERED", "createdAt": 1730000500000, "published": false}
                                                    ]
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
                            description = "Pedido não encontrado ou sem eventos",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AppErrorResponse.class)
                            )
                    )
            }
    )
    @BadRequestResponse
    public ResponseEntity<List<EventResponse>> getEvents(@PathVariable Long id) {
        LOGGER.info("Buscando eventos para o pedido ID: {}", id);

        var events = getOrderEventsService.getEvents(OrderId.of(id));
        return ResponseEntity.ok(events.stream().map(EventResponse::of).toList());
    }

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
