package com.danielpgbrasil.orderprocessing.infrastructure.controller;

import com.danielpgbrasil.orderprocessing.application.order.GetOrderStatusService;
import com.danielpgbrasil.orderprocessing.domain.order.OrderId;
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

@Tag(name = "Pedidos")
@RestController
@RequestMapping("/orders")
public class GetOrderStatusController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetOrderStatusController.class);

    private final GetOrderStatusService getOrderStatusService;

    public GetOrderStatusController(GetOrderStatusService getOrderStatusService) {
        this.getOrderStatusService = getOrderStatusService;
    }

    @GetMapping("/{id}/status")
    @Operation(
            summary = "Obtém o status atual de um pedido",
            description = "Retorna apenas o status do pedido informado pelo ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Status obtido com sucesso",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Response.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Exemplo de resposta",
                                                    value = """
                                                    {
                                                      "status": "IN_TRANSIT"
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
    public ResponseEntity<Response> getStatus(@PathVariable Long id) {
        LOGGER.info("Buscando status do pedido com ID: {}", id);
        var status = getOrderStatusService.getStatus(OrderId.of(id));
        return ResponseEntity.ok(new Response(status.name()));
    }

    @Schema(name = "GetOrderStatusResponse")
    public record Response(
            @Schema(description = "Status atual do pedido") String status
    ) {}
}
