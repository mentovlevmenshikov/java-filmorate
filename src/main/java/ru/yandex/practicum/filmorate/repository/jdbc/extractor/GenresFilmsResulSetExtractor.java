package ru.yandex.practicum.filmorate.repository.jdbc.extractor;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class GenresFilmsResulSetExtractor implements ResultSetExtractor<Map<Long, LinkedHashSet<Genre>>> {

    private final RowMapper<Genre> genreRowMapper;

    @Override
    public Map<Long, LinkedHashSet<Genre>> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, LinkedHashSet<Genre>> filmsGenresMap = new LinkedHashMap<>();
        while (rs.next()) {
            Long filmId = rs.getLong("film_id");
            filmsGenresMap.putIfAbsent(filmId, new LinkedHashSet<>());
            Genre genre = genreRowMapper.mapRow(rs, rs.getRow());
            filmsGenresMap.get(filmId).add(genre);
        }
        return filmsGenresMap;
    }
}
