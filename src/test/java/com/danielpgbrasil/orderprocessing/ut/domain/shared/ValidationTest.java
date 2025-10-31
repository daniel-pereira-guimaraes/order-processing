package com.danielpgbrasil.orderprocessing.ut.domain.shared;

import com.danielpgbrasil.orderprocessing.domain.shared.Validation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidationTest {

    @Test
    void requiredReturnsValueWhenNotNull() {
        var value = "test";
        var result = Validation.required(value, "Value is required");
        assertThat(result, is(value));
    }

    @Test
    void requiredThrowsExceptionWhenValueIsNull() {
        var message = "Value is required";
        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> Validation.required(null, message)
        );
        assertThat(exception.getMessage(), is(message));
    }

    @ParameterizedTest
    @ValueSource(strings = {"  hello  ", "world"})
    void requireNonBlankReturnsTrimmedValueWhenValid(String input) {
        var result = Validation.requireNonBlank(input, "String is required");
        assertThat(result, is(input.trim()));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void requireNonBlankThrowsExceptionWhenNullOrBlank(String input) {
        var message = "String is required";
        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> Validation.requireNonBlank(input, message)
        );
        assertThat(exception.getMessage(), is(message));
    }

    @Test
    void requireNonEmptyReturnsCollectionWhenValid() {
        var list = List.of("item1", "item2");
        var result = Validation.requireNonEmpty(list, "Collection is required");
        assertThat(result, is(list));
    }

    @Test
    void requireNonEmptyThrowsExceptionWhenNull() {
        var message = "Collection is required";
        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> Validation.requireNonEmpty(null, message)
        );
        assertThat(exception.getMessage(), is(message));
    }

    @Test
    void requireNonEmptyThrowsExceptionWhenEmptyList() {
        var emptyCollection = Collections.emptyList();
        var message = "Collection is required";

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> Validation.requireNonEmpty(emptyCollection, message)
        );

        assertThat(exception.getMessage(), is(message));
    }

    @Test
    void requireNonEmptyThrowsExceptionWhenEmptySet() {
        var emptySet = Set.of();
        var message = "Collection is required";

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> Validation.requireNonEmpty(emptySet, message)
        );

        assertThat(exception.getMessage(), is(message));
    }
}
