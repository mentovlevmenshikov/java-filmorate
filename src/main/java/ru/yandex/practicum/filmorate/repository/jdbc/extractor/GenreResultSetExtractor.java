package ru.yandex.practicum.filmorate.repository.jdbc.extractor;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class GenreResultSetExtractor implements ResultSetExtractor<Map<Long, Genre>> {

    private final RowMapper<Genre> genreRowMapper;

    public GenreResultSetExtractor(RowMapper<Genre> genreRowMapper) {
        this.genreRowMapper = genreRowMapper;
    }

    @Override
    public Map<Long, Genre> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, Genre> genreMap = new LinkedHashMap<>();
        while (rs.next()) {
            Long id = rs.getLong("genre_id");
            Genre genre = genreRowMapper.mapRow(rs, rs.getRow());
            genreMap.put(id, genre);
        }
        return genreMap;
    }
}
