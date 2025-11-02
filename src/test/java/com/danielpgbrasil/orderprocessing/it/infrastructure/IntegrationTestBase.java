package com.danielpgbrasil.orderprocessing.it.infrastructure;

import com.danielpgbrasil.orderprocessing.domain.shared.AppClock;
import com.danielpgbrasil.orderprocessing.domain.shared.TimeMillis;
import liquibase.integration.spring.SpringLiquibase;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

import static org.mockito.Mockito.when;

@SpringBootTest
@Import(MocksConfig.class)
public class IntegrationTestBase {

    @Autowired
    protected AppClock clock;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private SpringLiquibase liquibase;

    @BeforeEach
    void beforeEach() throws Exception {
        when(clock.now()).thenReturn(TimeMillis.of(0L));
        resetDatabase();
    }

    private void resetDatabase() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("DROP ALL OBJECTS;");
            }
        }
        liquibase.afterPropertiesSet();
    }

}
