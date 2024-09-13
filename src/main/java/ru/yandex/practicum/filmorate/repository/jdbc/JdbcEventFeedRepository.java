package ru.yandex.practicum.filmorate.repository.jdbc;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.UserEvent;
import ru.yandex.practicum.filmorate.repository.EventFeedRepository;
import ru.yandex.practicum.filmorate.repository.jdbc.mapper.UserEventRowMapper;

import java.util.Collection;
import java.util.Map;

@Repository
public class JdbcEventFeedRepository implements EventFeedRepository {
    private final NamedParameterJdbcOperations jdbc;
    private final UserEventRowMapper userEventRowMapper;

    public JdbcEventFeedRepository(NamedParameterJdbcOperations jdbc, UserEventRowMapper userEventRowMapper) {
        this.userEventRowMapper = userEventRowMapper;
        this.jdbc = jdbc;
    }

    @Override
    public Collection<UserEvent> getEventFeed(long userId) {
        String sql = """
                        SELECT event_timestamp, user_id, event_type, event_id, operation, entity_id
                        FROM event_feed
                        WHERE user_id = :userId
                    """;
        return jdbc.query(sql, Map.of("userId", userId), userEventRowMapper);
    }

    @Override
    public void addEvent(UserEvent event) {
        String sql = """
                    insert into event_feed (event_timestamp, user_id, event_type, operation,  entity_id)
                    values (:event_timestamp, :user_id, :event_type, :operation, :entity_id)
                    """;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("event_timestamp", event.getTimestamp());
        params.addValue("user_id", event.getUserId());
        params.addValue("event_type", event.getEventType().toString());
        params.addValue("operation", event.getOperation().toString());
        params.addValue("entity_id", event.getEntityId());
        jdbc.update(sql, params);
    }
}

