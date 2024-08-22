package ru.yandex.practicum.filmorate.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import ru.yandex.practicum.filmorate.repository.ModelRepository;

public abstract class JdbcBaseRepository<T> implements ModelRepository<T> {
    @Autowired
    protected NamedParameterJdbcOperations jdbc;
    protected final RowMapper<T> rowMapper;

    public JdbcBaseRepository(RowMapper<T> rowMapper) {
        this.rowMapper = rowMapper;
    }
}
