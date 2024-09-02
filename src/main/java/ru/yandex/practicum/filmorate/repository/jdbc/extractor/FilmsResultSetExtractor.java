package ru.yandex.practicum.filmorate.repository.jdbc.extractor;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class FilmsResultSetExtractor implements ResultSetExtractor<Map<Long, Film>> {

    private final RowMapper<Film> filmRowMapper;

    @Override
    public Map<Long, Film> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, Film> filmMap = new LinkedHashMap<>();
        while (rs.next()) {
            Long id = rs.getLong("film_id");
            Film film = filmRowMapper.mapRow(rs, rs.getRow());
            filmMap.put(id, film);
        }
        return filmMap;
    }
}
