package ru.yandex.practicum.filmorate.repository.jdbc.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.UserEvent;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UserEventRowMapper implements RowMapper<UserEvent> {

    @Override
    public UserEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
        return UserEvent.builder()
                .eventId(rs.getLong("event_id"))
                .timestamp(rs.getLong("event_timestamp"))
                .userId(rs.getLong("user_id"))
                .eventType(EventType.valueOf(rs.getString("event_type")))
                .operation(EventOperation.valueOf(rs.getString("operation")))
                .entityId(rs.getLong("entity_id"))
                .build();
    }
}
