package com.danielpgbrasil.orderprocessing.infrastructure.jdbc;

import com.danielpgbrasil.orderprocessing.domain.order.*;
import com.danielpgbrasil.orderprocessing.infrastructure.shared.Serializer;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcOrderRepository implements OrderRepository {

    private static final String SQL_INSERT = """
            INSERT INTO tb_order (details, status)
            VALUES (:details, :status)
            """;

    private static final String SQL_UPDATE = """
            UPDATE tb_order SET details = :details, status = :status
            WHERE id = :id
            """;

    private static final String SQL_SELECT_BASE = "SELECT id, details, status FROM tb_order";
    private static final String SQL_SELECT_BY_ID = SQL_SELECT_BASE + " WHERE id = :id FOR UPDATE";

    private static final String ID = "id";
    private static final String DETAILS = "details";
    private static final String STATUS = "status";

    private final NamedParameterJdbcTemplate jdbc;
    private final Serializer serializer;
    private final OrderListener orderListener;

    public JdbcOrderRepository(NamedParameterJdbcTemplate jdbc,
                               Serializer serializer,
                               OrderListener orderListener) {
        this.jdbc = jdbc;
        this.serializer = serializer;
        this.orderListener = orderListener;
    }

    @Override
    public Optional<Order> get(OrderId id) {
        try {
            var params = Map.of(ID, id.value());
            return Optional.of(jdbc.queryForObject(SQL_SELECT_BY_ID, params, (rs, rowNum) -> mapOrder(rs)));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Order getOrThrow(OrderId id) {
        return get(id).orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Override
    public void save(Order order) {
        if (order.id() == null) {
            insert(order);
        } else {
            update(order);
        }
    }

    private void insert(Order order) {
        var keyHolder = new CustomKeyHolder();
        jdbc.update(SQL_INSERT, new MapSqlParameterSource()
                .addValue(DETAILS, serializer.serialize(order.details()))
                .addValue(STATUS, order.status().name()), keyHolder);
        order.finalizeCreation(OrderId.of(keyHolder.asLong()));
    }

    private void update(Order order) {
        var params = new MapSqlParameterSource()
                .addValue(ID, order.id().value())
                .addValue(DETAILS, serializer.serialize(order.details()))
                .addValue(STATUS, order.status().name());
        jdbc.update(SQL_UPDATE, params);
    }

    private Order mapOrder(java.sql.ResultSet rs) throws SQLException {
        var detailsJson = rs.getString(DETAILS);
        var details = serializer.deserialize(detailsJson, OrderDetails.class);
        return Order.builder()
                .withId(OrderId.of(rs.getLong(ID)))
                .withDetails(details)
                .withStatus(OrderStatus.valueOf(rs.getString(STATUS)))
                .withListener(orderListener)
                .build();
    }

}
