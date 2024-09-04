package ru.yandex.practicum.filmorate.repository.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Repository
public class JdbcMpaRepository extends JdbcBaseRepository<MPA> {

    public JdbcMpaRepository(RowMapper<MPA> mpaRowMapper) {
        super(mpaRowMapper);
    }

    @Override
    public Optional<MPA> getById(long id) {
        String sql = "select mpa_id, mpa_name from mpa where mpa_id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        MPA mpa;

        try {
            mpa = jdbc.queryForObject(sql, params, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            log.warn("MPA with id {} not found", id);
            mpa = null;
        }

        return Optional.ofNullable(mpa);
    }

    @Override
    public Collection<MPA> getAll() {
        String sql = "select mpa_id, mpa_name from mpa order by mpa_id";
       return jdbc.query(sql, rowMapper);
    }

    @Override
    public MPA create(MPA model) {
        String sql = "INSERT INTO mpa (mpa_name) VALUES (:name)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", model.getName());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder);
        model.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return model;
    }

    @Override
    public MPA update(MPA model) {
        String sql = "UPDATE mpa SET mpa_name = :name WHERE mpa_id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", model.getId());
        params.addValue("name", model.getName());
        int countUpdate = jdbc.update(sql, params);
        if (countUpdate == 0) {
            throw new NotFoundException("MPA with id " + model.getId() + " not found");
        }
        return model;
    }

    @Override
    public void delete(long id) {
    }
}
