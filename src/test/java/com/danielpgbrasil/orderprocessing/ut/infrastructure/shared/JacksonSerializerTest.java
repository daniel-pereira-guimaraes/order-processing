package com.danielpgbrasil.orderprocessing.ut.infrastructure.shared;

import com.danielpgbrasil.orderprocessing.domain.order.OrderDetails;
import com.danielpgbrasil.orderprocessing.domain.order.OrderItem;
import com.danielpgbrasil.orderprocessing.infrastructure.shared.JacksonSerializer;
import com.danielpgbrasil.orderprocessing.infrastructure.shared.UncheckedSerializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JacksonSerializerTest {

    private JacksonSerializer serializer;

    private static final String CUSTOMER_NAME = "John Doe";
    private static final String CUSTOMER_ADDRESS = "123 Main Street";
    private static final Long PRODUCT_ID = 1L;
    private static final Integer QUANTITY = 2;
    private static final BigDecimal PRICE = new BigDecimal("10.50");

    private static final String EXPECTED_JSON = """
        {
          "customerName":"John Doe",
          "customerAddress":"123 Main Street",
          "items":[
            {
              "productId":1,
              "quantity":2,
              "price":10.50
            }
          ]
        }
        """;

    @BeforeEach
    void setUp() {
        serializer = new JacksonSerializer();
    }

    private OrderItem createOrderItem() {
        return OrderItem.builder()
                .withProductId(PRODUCT_ID)
                .withQuantity(QUANTITY)
                .withPrice(PRICE)
                .build();
    }

    private OrderDetails createOrderDetails() {
        return OrderDetails.builder()
                .withCustomerName(CUSTOMER_NAME)
                .withCustomerAddress(CUSTOMER_ADDRESS)
                .withItems(List.of(createOrderItem()))
                .build();
    }

    @Test
    void serializeReturnsJsonStringWhenOrderDetailsProvided() throws Exception {
        var orderDetails = createOrderDetails();

        var jsonResult = serializer.serialize(orderDetails);

        JSONAssert.assertEquals(EXPECTED_JSON, jsonResult, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    void deserializeReturnsOrderDetailsFromJson() {
        var orderDetails = serializer.deserialize(EXPECTED_JSON, OrderDetails.class);

        assertThat(orderDetails.customerName(), is(CUSTOMER_NAME));
        assertThat(orderDetails.customerAddress(), is(CUSTOMER_ADDRESS));

        var item = orderDetails.items().getFirst();
        assertThat(item.productId(), is(PRODUCT_ID));
        assertThat(item.quantity(), is(QUANTITY));
        assertThat(item.price().compareTo(PRICE), is(0));
    }

    @Test
    void serializeThrowsUncheckedSerializationExceptionWhenObjectIsInvalid() {
        var obj = new CircularReferenceStub();

        var exception = assertThrows(UncheckedSerializationException.class,
                () -> serializer.serialize(obj)
        );
        assertThat(exception.getCause(), is(notNullValue()));
    }

    public static class CircularReferenceStub {
        @SuppressWarnings("unused")
        private final Object self = this;
    }
}
