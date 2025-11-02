package com.danielpgbrasil.orderprocessing.it.infrastructure.controller;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GetOrderStatusControllerTest extends ControllerTestBase {

    private static final String ENDPOINT = "/orders/%s/status";

    @ParameterizedTest
    @CsvSource({
            "1, CREATED",
            "2, PICKING",
            "3, IN_TRANSIT",
            "4, DELIVERED"
    })
    void getStatusReturnsCorrectData(long orderId, String expectedStatus) throws Exception {
        mockMvc.perform(get(ENDPOINT.formatted(orderId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$.status").value(expectedStatus));
    }

    @ParameterizedTest
    @CsvSource({
            "999, 404",
            "abc, 400"
    })
    void getStatusInvalidInputsReturnErrors(String orderId, int expectedStatusCode) throws Exception {
        mockMvc.perform(get(ENDPOINT.formatted(orderId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(expectedStatusCode));
    }
}
