package ru.yandex.practicum.filmorate.repository.jdbc.extractor;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class DirectorsFilmsResulSetExtractor implements ResultSetExtractor<Map<Long, HashSet<Director>>> {

    private final RowMapper<Director> directorRowMapper;

    @Override
    public Map<Long, HashSet<Director>> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, HashSet<Director>> filmsDirectorsMap = new HashMap<>();
        while (rs.next()) {
            Long filmId = rs.getLong("film_id");
            filmsDirectorsMap.putIfAbsent(filmId, new HashSet<>());
            Director director = directorRowMapper.mapRow(rs, rs.getRow());
            filmsDirectorsMap.get(filmId).add(director);
        }
        return filmsDirectorsMap;
    }
}
