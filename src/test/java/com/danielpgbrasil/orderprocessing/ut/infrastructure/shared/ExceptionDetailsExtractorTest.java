package com.danielpgbrasil.orderprocessing.ut.infrastructure.shared;

import com.danielpgbrasil.orderprocessing.infrastructure.shared.ExceptionDetailsExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ExceptionDetailsExtractorTest {

    private ExceptionDetailsExtractor extractor;

    @BeforeEach
    void setUp() {
        extractor = new ExceptionDetailsExtractor();
    }

    @Test
    void rootCauseMessageReturnsMessageWithClassNameWhenExceptionHasMessage() {
        var ex = new RuntimeException("Something went wrong");

        var result = extractor.rootCauseMessage(ex);

        assertThat(result, is("java.lang.RuntimeException: Something went wrong"));
    }

    @Test
    void rootCauseMessageReturnsOnlyClassNameWhenExceptionHasNoMessage() {
        var ex = new RuntimeException();

        var result = extractor.rootCauseMessage(ex);

        assertThat(result, is("java.lang.RuntimeException"));
    }

    @Test
    void rootCauseMessageReturnsRootCauseWhenExceptionIsNested() {
        var root = new IllegalStateException("Root cause");
        var middle = new RuntimeException("Middle", root);
        var top = new Exception("Top", middle);

        var result = extractor.rootCauseMessage(top);

        assertThat(result, is("java.lang.IllegalStateException: Root cause"));
    }

    @Test
    void stackTraceToStringIncludesClassNameAndMessage() {
        var ex = new RuntimeException("Error");

        var result = extractor.stackTraceToString(ex);

        assertThat(result, containsString("java.lang.RuntimeException: Error"));
        assertThat(result, containsString("at "));
    }

    @Test
    void stackTraceToStringIncludesRootCauseWhenNested() {
        var root = new IllegalArgumentException("Bad arg");
        var wrapper = new RuntimeException("Wrapper", root);

        var result = extractor.stackTraceToString(wrapper);

        assertThat(result, containsString("java.lang.RuntimeException: Wrapper"));
        assertThat(result, containsString("java.lang.IllegalArgumentException: Bad arg"));
    }
}
