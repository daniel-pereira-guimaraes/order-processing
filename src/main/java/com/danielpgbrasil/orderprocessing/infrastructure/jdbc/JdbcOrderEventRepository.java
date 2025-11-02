package com.danielpgbrasil.orderprocessing.infrastructure.jdbc;

import com.danielpgbrasil.orderprocessing.domain.order.OrderId;
import com.danielpgbrasil.orderprocessing.domain.order.event.*;
import com.danielpgbrasil.orderprocessing.domain.shared.TimeMillis;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcOrderEventRepository implements OrderEventRepository {

    private static final String SQL_INSERT = """
            INSERT INTO tb_order_event (order_id, type, created_at, published)
            VALUES (:order_id, :type, :created_at, :published)
            """;

    private static final String SQL_UPDATE = """
            UPDATE tb_order_event
            SET order_id = :order_id,
                type = :type,
                created_at = :created_at,
                published = :published
            WHERE id = :id
            """;

    private static final String SQL_SELECT_BASE = """
            SELECT id, order_id, type, created_at, published
            FROM tb_order_event
            """;

    private static final String SQL_SELECT_BY_ID = SQL_SELECT_BASE
            + " WHERE id = :id";

    private static final String SQL_SELECT_BY_ORDER_ID = SQL_SELECT_BASE
            + " WHERE order_id = :order_id ORDER BY id";

    private static final String SQL_SELECT_UNPUBLISHED = SQL_SELECT_BASE
            + " WHERE NOT published ORDER BY id FOR UPDATE";

    private static final String ID = "id";
    private static final String ORDER_ID = "order_id";
    private static final String TYPE = "type";
    private static final String CREATED_AT = "created_at";
    private static final String PUBLISHED = "published";

    private final NamedParameterJdbcTemplate jdbc;

    public JdbcOrderEventRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void save(OrderEvent event) {
        if (event.id() == null) {
            insert(event);
        } else {
            update(event);
        }
    }

    @Override
    public Optional<OrderEvent> get(OrderEventId id) {
        try {
            var params = Map.of(ID, id.value());
            return Optional.of(jdbc.queryForObject(SQL_SELECT_BY_ID, params, (rs, rowNum) -> mapEvent(rs)));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public OrderEvent getOrThrow(OrderEventId id) {
        return get(id).orElseThrow(() ->  new OrderEventNotFoundException(id));
    }

    @Override
    public List<OrderEvent> findAllUnpublished() {
        return jdbc.query(SQL_SELECT_UNPUBLISHED, (rs, rowNum) -> mapEvent(rs));
    }

    @Override
    public List<OrderEvent> findByOrderId(OrderId orderId) {
        var params = Map.of(ORDER_ID, orderId.value());
        return jdbc.query(SQL_SELECT_BY_ORDER_ID, params, (rs, rowNum) -> mapEvent(rs));
    }

    private void insert(OrderEvent event) {
        var keyHolder = new CustomKeyHolder();
        var params = new MapSqlParameterSource()
                .addValue(ORDER_ID, event.orderId().value())
                .addValue(TYPE, event.type().name())
                .addValue(CREATED_AT, event.createdAt().value())
                .addValue(PUBLISHED, event.isPublished());
        jdbc.update(SQL_INSERT, params, keyHolder);
        event.finalizeCreation(OrderEventId.of(keyHolder.asLong()));
    }

    private void update(OrderEvent event) {
        var params = new MapSqlParameterSource()
                .addValue(ID, event.id().value())
                .addValue(ORDER_ID, event.orderId().value())
                .addValue(TYPE, event.type().name())
                .addValue(CREATED_AT, event.createdAt().value())
                .addValue(PUBLISHED, event.isPublished());
        jdbc.update(SQL_UPDATE, params);
    }

    private OrderEvent mapEvent(java.sql.ResultSet rs) throws SQLException {
        return OrderEvent.builder()
                .withId(OrderEventId.of(rs.getLong(ID)))
                .withOrderId(OrderId.of(rs.getLong(ORDER_ID)))
                .withType(OrderEventType.valueOf(rs.getString(TYPE)))
                .withCreatedAt(TimeMillis.of(rs.getLong(CREATED_AT)))
                .withPublished(rs.getBoolean(PUBLISHED))
                .build();
    }
}
