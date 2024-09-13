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
import java.util.Map;

@RequiredArgsConstructor
@Component
public class DirectorResultSetExtractor implements ResultSetExtractor<Map<Long, Director>> {
    private final RowMapper<Director> directorRowMapper;

    @Override
    public Map<Long, Director> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, Director> directorMap = new HashMap<>();
        while (rs.next()) {
            Long id = rs.getLong("director_id");
            Director director = directorRowMapper.mapRow(rs, rs.getRow());
            directorMap.put(id, director);
        }
        return directorMap;
    }

}
