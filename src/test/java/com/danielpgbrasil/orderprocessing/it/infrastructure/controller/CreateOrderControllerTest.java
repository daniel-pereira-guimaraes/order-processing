package com.danielpgbrasil.orderprocessing.it.infrastructure.controller;

import com.danielpgbrasil.orderprocessing.domain.order.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CreateOrderControllerTest extends ControllerTestBase {

    private static final String ENDPOINT = "/orders";

    private static final String CUSTOMER_NAME = "Joao Silva";
    private static final String CUSTOMER_ADDRESS = "Rua A, 123";

    private static final String VALID_REQUEST_BODY = """
            {
              "customerName": "%s",
              "customerAddress": "%s",
              "items": [
                {"productId": 1, "quantity": 2, "price": 50.0},
                {"productId": 2, "quantity": 1, "price": 30.0}
              ]
            }
            """;

    private static final String REQUEST_BODY_MISSING_NAME = """
            {
              "customerAddress": "Rua A, 123",
              "items": [{"productId":1,"quantity":2,"price":50.0}]
            }
            """;

    private static final String REQUEST_BODY_EMPTY_ITEMS = """
            {
              "customerName": "Joao Silva",
              "customerAddress": "Rua A, 123",
              "items": []
            }
            """;

    private static final String REQUEST_BODY_ZERO_QTY = """
            {
              "customerName": "Joao Silva",
              "customerAddress": "Rua A, 123",
              "items": [{"productId":1,"quantity":0,"price":50.0}]
            }
            """;

    private static final String REQUEST_BODY_NEG_PRICE = """
            {
              "customerName": "Joao Silva",
              "customerAddress": "Rua A, 123",
              "items": [{"productId":1,"quantity":1,"price":-10.0}]
            }
            """;

    private static final String EXPECTED_RESPONSE_BODY = """
        {
          "id": %d,
          "customerName": "Joao Silva",
          "customerAddress": "Rua A, 123",
          "items": [
            {"productId": 1, "quantity": 2, "price": 50.0},
            {"productId": 2, "quantity": 1, "price": 30.0}
          ]
        }
        """;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void createOrderReturnsCreatedOrder() throws Exception {
        var requestBody = String.format(VALID_REQUEST_BODY, CUSTOMER_NAME, CUSTOMER_ADDRESS);

        var response = performPost(requestBody)
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        var orderId = extractIdFromResponse(response);

        var expectedOrder = buildExpectedOrder(orderId);
        var savedOrder = orderRepository.get(expectedOrder.id()).orElseThrow();
        assertThat(savedOrder, equalTo(expectedOrder));

        var expectedJson = String.format(EXPECTED_RESPONSE_BODY, orderId);
        JSONAssert.assertEquals(expectedJson, response, true);
    }

    private static Stream<String> invalidOrderRequests() {
        return Stream.of(
                REQUEST_BODY_MISSING_NAME,
                REQUEST_BODY_EMPTY_ITEMS,
                REQUEST_BODY_ZERO_QTY,
                REQUEST_BODY_NEG_PRICE
        );
    }

    @ParameterizedTest
    @MethodSource("invalidOrderRequests")
    void returnsBadRequestForInvalidRequests(String requestBody) throws Exception {
        performPost(requestBody)
                .andExpect(status().isBadRequest());
    }

    private Order buildExpectedOrder(Long orderId) {
        return Order.builder()
                .withId(OrderId.of(orderId))
                .withDetails(buildExpectedOrderDetails())
                .withStatus(OrderStatus.CREATED)
                .withListener(mock(OrderListener.class))
                .build();
    }

    private OrderDetails buildExpectedOrderDetails() {
        return OrderDetails.builder()
                .withCustomerName(CUSTOMER_NAME)
                .withCustomerAddress(CUSTOMER_ADDRESS)
                .withItems(buildExpectedItems())
                .build();
    }

    private List<OrderItem> buildExpectedItems() {
        return List.of(
                orderItem(1L, 2, 50.0),
                orderItem(2L, 1, 30.0)
        );
    }

    private OrderItem orderItem(long productId, int quantity, double price) {
        return OrderItem.builder()
                .withProductId(productId)
                .withQuantity(quantity)
                .withPrice(BigDecimal.valueOf(price))
                .build();
    }

    private org.springframework.test.web.servlet.ResultActions performPost(String requestBody) throws Exception {
        return mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));
    }

    private Long extractIdFromResponse(String response) throws JSONException {
        return new JSONObject(response).getLong("id");
    }
}
