package ru.yandex.practicum.filmorate.repository.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Repository
public class JdbcDirectorRepository extends JdbcBaseRepository<Director> {

    public JdbcDirectorRepository(RowMapper<Director> rowMapper) {
        super(rowMapper);
    }

    @Override
    public Optional<Director> getById(long id) {
        String sql = "SELECT director_id, director_name FROM directors WHERE director_id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        Director director;

        try {
            director = jdbc.queryForObject(sql, params, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Director with id {} not found", id);
            director = null;
        }

        return Optional.ofNullable(director);
    }

    @Override
    public Collection<Director> getAll() {
        String sql = "SELECT director_id, director_name FROM directors ORDER BY director_id";
        return jdbc.query(sql, rowMapper);
    }

    @Override
    public Director create(Director model) {
        String sql = "INSERT INTO directors (director_name) VALUES (:name)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", model.getName());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder);
        model.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return model;
    }

    @Override
    public Director update(Director model) {
        String sql = "UPDATE directors SET director_name = :name WHERE director_id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", model.getId());
        params.addValue("name", model.getName());
        int countUpdate = jdbc.update(sql, params);
        if (countUpdate == 0) {
            throw new NotFoundException("Director with id " + model.getId() + " not found");
        }
        return model;
    }
}
