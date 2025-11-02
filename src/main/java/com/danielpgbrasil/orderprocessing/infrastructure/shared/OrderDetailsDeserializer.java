package com.danielpgbrasil.orderprocessing.infrastructure.shared;

import com.danielpgbrasil.orderprocessing.domain.order.OrderDetails;
import com.danielpgbrasil.orderprocessing.domain.order.OrderItem;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailsDeserializer extends JsonDeserializer<OrderDetails> {

    @Override
    public OrderDetails deserialize(JsonParser jsonParser,
                                    DeserializationContext context)
            throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        var customerName = node.get("customerName").asText();
        var customerAddress = node.get("customerAddress").asText();

        var itemsNode = node.get("items");
        List<OrderItem> items = new ArrayList<>();

        for (JsonNode itemNode : itemsNode) {
            var productId = itemNode.get("productId").asLong();
            var quantity = itemNode.get("quantity").asInt();
            var price = new BigDecimal(itemNode.get("price").asText());

            var item = OrderItem.builder()
                    .withProductId(productId)
                    .withQuantity(quantity)
                    .withPrice(price)
                    .build();
            items.add(item);
        }

        return OrderDetails.builder()
                .withCustomerName(customerName)
                .withCustomerAddress(customerAddress)
                .withItems(items)
                .build();
    }
}
