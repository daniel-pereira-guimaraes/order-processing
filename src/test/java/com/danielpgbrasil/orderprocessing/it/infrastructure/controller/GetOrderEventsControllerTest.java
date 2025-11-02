package com.danielpgbrasil.orderprocessing.it.infrastructure.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GetOrderEventsControllerTest extends ControllerTestBase {

    private static final String ENDPOINT = "/orders/%s/events";

    private static final int EXISTING_ORDER_ID = 4;

    private static final String EXPECTED_ORDER_EVENTS = """
        [
          {"id": 1, "type": "CREATED", "createdAt": 1700000300000, "published": false},
          {"id": 10, "type": "PICKING_STARTED", "createdAt": 1700000305000, "published": true},
          {"id": 11, "type": "TRANSIT_STARTED", "createdAt": 1700000310000, "published": true},
          {"id": 12, "type": "DELIVERED", "createdAt": 1700000315000, "published": true}
        ]
        """;

    @Test
    void getEventsByOrderIdReturnsCorrectData() throws Exception {
        var response = mockMvc.perform(get(ENDPOINT.formatted(EXISTING_ORDER_ID))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JSONAssert.assertEquals(EXPECTED_ORDER_EVENTS, response, JSONCompareMode.STRICT_ORDER);
    }

    @ParameterizedTest
    @CsvSource({
            "999,404",
            "abc,400"
    })
    void getEventsWithInvalidOrUnknownOrderIdReturnsError(String orderId, int expectedStatus) throws Exception {
        mockMvc.perform(get(ENDPOINT.formatted(orderId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(expectedStatus));
    }
}
