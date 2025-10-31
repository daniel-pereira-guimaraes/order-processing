package com.danielpgbrasil.orderprocessing.ut.domain.order;

import com.danielpgbrasil.orderprocessing.domain.order.OrderDetails;
import com.danielpgbrasil.orderprocessing.fixture.OrderItemFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Stream;

import static com.danielpgbrasil.orderprocessing.fixture.OrderDetailsFixture.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderDetailsTest {

    @Test
    void builderCreatesOrderDetailsWhenValid() {
        var orderDetails = builder().build();

        assertThat(orderDetails.customerName(), is(CUSTOMER_NAME));
        assertThat(orderDetails.customerAddress(), is(CUSTOMER_ADDRESS));
        assertThat(orderDetails.items(), hasSize(1));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void builderThrowsExceptionWhenCustomerNameBlank(String name) {
        var builder = builder().withCustomerName(name);

        var exception = assertThrows(IllegalArgumentException.class, builder::build);

        assertThat(exception.getMessage(), is("O nome do cliente é requerido."));
    }

    @Test
    void builderThrowsExceptionWhenCustomerNameNull() {
        var builder = builder().withCustomerName(null);

        var exception = assertThrows(IllegalArgumentException.class, builder::build);

        assertThat(exception.getMessage(), is("O nome do cliente é requerido."));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void builderThrowsExceptionWhenCustomerAddressBlank(String address) {
        var builder = builder().withCustomerAddress(address);

        var exception = assertThrows(IllegalArgumentException.class, builder::build);

        assertThat(exception.getMessage(), is("O endereço do cliente é requerido."));
    }

    @Test
    void builderThrowsExceptionWhenCustomerAddressNull() {
        var builder = builder().withCustomerAddress(null);

        var exception = assertThrows(IllegalArgumentException.class, builder::build);

        assertThat(exception.getMessage(), is("O endereço do cliente é requerido."));
    }

    @Test
    void builderThrowsExceptionWhenItemsEmpty() {
        var builder = builder().withItems(List.of());

        var exception = assertThrows(IllegalArgumentException.class, builder::build);

        assertThat(exception.getMessage(), is("Os itens do pedido são requeridos."));
    }

    @Test
    void builderThrowsExceptionWhenItemsNull() {
        var builder = builder().withItems(null);

        var exception = assertThrows(IllegalArgumentException.class, builder::build);

        assertThat(exception.getMessage(), is("Os itens do pedido são requeridos."));
    }

    @Test
    void itemsIsUnmodifiableWhenBuilt() {
        var items = builder().build().items();
        var newItem = OrderItemFixture.builder().build();

        var exception = assertThrows(UnsupportedOperationException.class,
                () -> items.add(newItem)
        );

        assertThat(exception, instanceOf(UnsupportedOperationException.class));
    }

    static Stream<Arguments> sameReferenceProvider() {
        var details = builder().build();
        return Stream.of(Arguments.of(details, details));
    }

    @ParameterizedTest
    @MethodSource("sameReferenceProvider")
    void equalsReturnsTrueForSameReference(OrderDetails a, OrderDetails b) {
        assertThat(a.equals(b), is(true));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "dummy"})
    void equalsReturnsFalseForNullOrDifferentClass(Object other) {
        var details = builder().build();

        assertThat(details.equals(other), is(false));
    }

    @Test
    void equalsReturnsTrueForEqualOrderDetails() {
        var details1 = builder().build();
        var details2 = builder().build();

        assertThat(details1.equals(details2), is(true));
    }

    @Test
    void equalsReturnsFalseForDifferentCustomerName() {
        var details1 = builder().withCustomerName("Name1").build();
        var details2 = builder().withCustomerName("Name2").build();

        assertThat(details1.equals(details2), is(false));
    }

    @Test
    void equalsReturnsFalseForDifferentCustomerAddress() {
        var details1 = builder().withCustomerAddress("Address1").build();
        var details2 = builder().withCustomerAddress("Address2").build();

        assertThat(details1.equals(details2), is(false));
    }

    @Test
    void equalsReturnsFalseForDifferentItems() {
        var item1 = OrderItemFixture.builder().withProductId(1L).build();
        var item2 = OrderItemFixture.builder().withProductId(2L).build();
        var details1 = builder().withItems(List.of(item1)).build();
        var details2 = builder().withItems(List.of(item2)).build();

        assertThat(details1.equals(details2), is(false));
    }

    @Test
    void hashCodeIsEqualForEqualOrderDetails() {
        var details1 = builder().build();
        var details2 = builder().build();

        assertThat(details1.hashCode(), is(details2.hashCode()));
    }

    @Test
    void hashCodeIsDifferentForDifferentOrderDetails() {
        var details1 = builder().withCustomerName("Name1").build();
        var details2 = builder().withCustomerName("Name2").build();

        assertThat(details1.hashCode(), not(details2.hashCode()));
    }
}
