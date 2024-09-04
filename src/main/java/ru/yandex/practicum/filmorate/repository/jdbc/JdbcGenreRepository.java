package ru.yandex.practicum.filmorate.repository.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Repository
@Slf4j
public class JdbcGenreRepository extends JdbcBaseRepository<Genre> {

    public JdbcGenreRepository(RowMapper<Genre> genreRowMapper) {
        super(genreRowMapper);
    }

    @Override
    public Optional<Genre> getById(long id) {
        String sql = "select genre_id, genre_name from GENRES where genre_id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        Genre genre;
        try {
            genre = jdbc.queryForObject(sql, params, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Genre with id {} not found", id);
            return Optional.empty();
        }
        return Optional.ofNullable(genre);
    }

    @Override
    public Collection<Genre> getAll() {
        String sql = "select genre_id, genre_name from GENRES order by genre_id";
        return jdbc.query(sql, rowMapper);
    }

    @Override
    public Genre create(Genre model) {
        String sql = "insert into GENRES (genre_name) values (:genre_name)";
        MapSqlParameterSource params = new MapSqlParameterSource("genre_name", model.getName());
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder);
        model.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return model;
    }

    @Override
    public Genre update(Genre model) {
        String sql = "update GENRES set genre_name = :genre_name where genre_id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", model.getId());
        params.addValue("genre_name", model.getName());
        int countUpdate = jdbc.update(sql, params);
        if (countUpdate == 0) {
            throw new NotFoundException("Genre with id " + model.getId() + " not found");
        }
        return model;
    }

    @Override
    public void delete(long id) {
    }
}