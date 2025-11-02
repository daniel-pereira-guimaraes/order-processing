package com.danielpgbrasil.orderprocessing.infrastructure.controller;

import com.danielpgbrasil.orderprocessing.application.order.CreateOrderService;
import com.danielpgbrasil.orderprocessing.domain.order.Order;
import com.danielpgbrasil.orderprocessing.domain.order.OrderDetails;
import com.danielpgbrasil.orderprocessing.domain.order.OrderItem;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "Pedidos")
@RestController
@RequestMapping("/orders")
public class CreateOrderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateOrderController.class);

    private final CreateOrderService createOrderService;

    public CreateOrderController(CreateOrderService createOrderService) {
        this.createOrderService = createOrderService;
    }

    @PostMapping
    @Operation(
            summary = "Cria um pedido",
            description = "Cria um novo pedido com detalhes do cliente e itens",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do pedido",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Request.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Pedido exemplo 1",
                                            value = """
                                                {
                                                  "customerName": "Joao Silva",
                                                  "customerAddress": "Rua A, 123",
                                                  "items": [
                                                    {"productId": 1, "quantity": 2, "price": 50.0},
                                                    {"productId": 2, "quantity": 1, "price": 30.0}
                                                  ]
                                                }
                                                """
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Pedido criado com sucesso",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Response.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Requisição inválida",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AppErrorResponse.class)
                            )
                    )
            }
    )
    @BadRequestResponse
    public ResponseEntity<Response> post(@RequestBody Request request) {
        LOGGER.info("Criando pedido para cliente: {}", request.customerName());

        var details = request.toOrderDetails();
        var order = createOrderService.createOrder(details);

        return ResponseEntity.status(HttpStatus.CREATED).body(Response.of(order));
    }

    @Schema(name = "CreateOrderRequest")
    public record Request(
            @Schema(description = "Nome do cliente", example = "Joao Silva")
            String customerName,
            @Schema(description = "Endereço do cliente", example = "Rua A, 123")
            String customerAddress,
            @Schema(description = "Itens do pedido")
            List<Item> items
    ) {
        public OrderDetails toOrderDetails() {
            return OrderDetails.builder()
                    .withCustomerName(customerName)
                    .withCustomerAddress(customerAddress)
                    .withItems(items.stream()
                            .map(Item::toOrderItem)
                            .toList())
                    .build();
        }

        @Schema(name = "CreateOrderItemRequest")
        public record Item(
                @Schema(description = "ID do produto", example = "1")
                Long productId,
                @Schema(description = "Quantidade do produto", example = "2")
                Integer quantity,
                @Schema(description = "Preço do produto", example = "50.0")
                BigDecimal price
        ) {
            public OrderItem toOrderItem() {
                return OrderItem.builder()
                        .withProductId(productId)
                        .withQuantity(quantity)
                        .withPrice(price)
                        .build();
            }
        }
    }

    @Schema(name = "CreateOrderResponse")
    public record Response(
            @Schema(description = "ID do pedido", example = "1")
            Long id,
            @Schema(description = "Nome do cliente", example = "Joao Silva")
            String customerName,
            @Schema(description = "Endereço do cliente", example = "Rua A, 123")
            String customerAddress,
            @Schema(description = "Itens do pedido")
            List<Item> items) {

        public static Response of(Order order) {
            return new Response(
                    order.id().value(),
                    order.details().customerName(),
                    order.details().customerAddress(),
                    order.details().items().stream()
                            .map(i -> new Item(i.productId(), i.quantity(), i.price()))
                            .toList()
            );
        }
    }

    @Schema(name = "CreateOrderItemResponse")
    public record Item(
            Long productId,
            Integer quantity,
            BigDecimal price) {
    }

}
