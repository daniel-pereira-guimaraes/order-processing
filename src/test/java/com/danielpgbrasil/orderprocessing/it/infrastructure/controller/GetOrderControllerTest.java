package com.danielpgbrasil.orderprocessing.it.infrastructure.controller;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GetOrderControllerTest extends ControllerTestBase {

    private static final String ENDPOINT = "/orders";

    private static final String EXPECTED_ORDER_1_WITH_EVENTS = """
        {
          "order": {
            "id": 1,
            "customerName": "João Silva",
            "customerAddress": "Rua Exemplo, 123, São Paulo/SP",
            "items": [
              {"productId": 1, "quantity": 5, "price": 10.0}
            ],
            "status": "CREATED"
          },
          "events": [
            {"id": 2, "type": "PICKING_STARTED", "createdAt": 1700000005000, "published": true},
            {"id": 3, "type": "TRANSIT_STARTED", "createdAt": 1700000010000, "published": true},
            {"id": 9, "type": "CREATED", "createdAt": 1700000000000, "published": false}
          ]
        }
        """;

    private static final String EXPECTED_ORDER_2_NO_EVENTS = """
        {
          "order": {
            "id": 2,
            "customerName": "Maria Souza",
            "customerAddress": "Av. Central, 456, Rio de Janeiro/RJ",
            "items": [
              {"productId": 2, "quantity": 3, "price": 15.5},
              {"productId": 3, "quantity": 1, "price": 100.0}
            ],
            "status": "PICKING"
          },
          "events": []
        }
        """;

    @Test
    void getOrderWithItemsReturnsCorrectData() throws Exception {
        var response = mockMvc.perform(get(ENDPOINT + "/1")
                        .param("includeEvents", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals(EXPECTED_ORDER_1_WITH_EVENTS, response, true);
    }

    @Test
    void getOrderWithoutEventsReturnsCorrectData() throws Exception {
        var response = mockMvc.perform(get(ENDPOINT + "/2")
                        .param("includeEvents", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals(EXPECTED_ORDER_2_NO_EVENTS, response, true);
    }

    @Test
    void getOrderNotFoundReturns404() throws Exception {
        mockMvc.perform(get(ENDPOINT + "/999")
                        .param("includeEvents", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getOrderWithInvalidParamReturnsBadRequest() throws Exception {
        mockMvc.perform(get(ENDPOINT + "/abc")
                        .param("includeEvents", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
