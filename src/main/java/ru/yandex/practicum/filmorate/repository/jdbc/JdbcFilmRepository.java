package ru.yandex.practicum.filmorate.repository.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.LikeStorage;

import java.util.*;

@Repository
@Primary
@Slf4j
public class JdbcFilmRepository extends JdbcBaseRepository<Film> implements LikeStorage {

    private final RowMapper<Genre> genreRowMapper;
    private final ResultSetExtractor<Map<Long, LinkedHashSet<Genre>>> filmsGenresExtractor;

    public JdbcFilmRepository(RowMapper<Film> filmRowMapper, RowMapper<Genre> genreRowMapper,
                              ResultSetExtractor<Map<Long, LinkedHashSet<Genre>>> filmsGenresExtractor) {
        super(filmRowMapper);
        this.filmsGenresExtractor = filmsGenresExtractor;
        this.genreRowMapper = genreRowMapper;
    }

    @Override
    public Optional<Film> getById(long id) {
        String sqlFilm = """
                        SELECT f.film_Id, f.name, f.description, f.release_date, f.duration,
                               m.mpa_id, m.mpa_name,
                               (SELECT count(*) FROM FILMS_LIKES L WHERE L.FILM_ID = f.FILM_ID) count_likes
                        FROM films f JOIN MPA m ON f.mpa_id = m.mpa_id
                        WHERE f.film_id = :id
                    """;
        MapSqlParameterSource filmParams = new MapSqlParameterSource("id", id);
        Film film;
        try {
            film = jdbc.queryForObject(sqlFilm, filmParams, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Film with id {} not found", id);
            return Optional.empty();
        }

        String sqlGenre = """
                                SELECT g.genre_id, g.genre_name
                                FROM films_genres f JOIN genres g ON f.genre_id = g.genre_id
                                WHERE f.film_id = :film_id
                                ORDER BY G.GENRE_ID
                          """;
        MapSqlParameterSource genreParams = new MapSqlParameterSource("film_id", Objects.requireNonNull(film).getId());
        film.setGenres(new LinkedHashSet<>(jdbc.query(sqlGenre, genreParams, genreRowMapper)));
        return Optional.of(film);
    }

    @Override
    public Collection<Film> getAll() {
        String sqlFilms = """
                            SELECT f.film_Id, f.name, f.description, f.release_date, f.duration,
                                   m.mpa_id, m.mpa_name,
                                   (SELECT count(*) FROM FILMS_LIKES L WHERE L.FILM_ID = f.FILM_ID) count_likes
                            FROM films f JOIN MPA m ON f.mpa_id = m.mpa_id
                            order by f.film_Id
                           """;
        List<Film> films = jdbc.query(sqlFilms, rowMapper);
        if (films.isEmpty()) {
            return Collections.emptyList();
        }

        String sqlFilmsGenres = """
                SELECT f.film_id, g.genre_id, g.genre_name
                FROM films_genres f JOIN genres g ON f.genre_id = g.genre_id
                ORDER BY G.GENRE_ID
                """;
        Map<Long, LinkedHashSet<Genre>> filmsGenres = jdbc.query(sqlFilmsGenres, filmsGenresExtractor);
        if (filmsGenres != null && !filmsGenres.isEmpty()) {
            films.forEach(film -> {
                LinkedHashSet<Genre> genres = filmsGenres.get(film.getId());
                if (genres != null) {
                    film.setGenres(genres);
                }
            });
        }

        return films;
    }

    @Override
    public Film create(Film model) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sqlInsert = "INSERT INTO FILMS (NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID) " +
                "VALUES (:name, :description, :release_date, :duration, :mpa_id);";

        MapSqlParameterSource filmParams = new MapSqlParameterSource();
        filmParams.addValue("name", model.getName());
        filmParams.addValue("description", model.getDescription());
        filmParams.addValue("release_date", model.getReleaseDate());
        filmParams.addValue("duration", model.getDuration());
        filmParams.addValue("mpa_id", model.getMpa().getId());

        jdbc.update(sqlInsert, filmParams, keyHolder);
        model.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        if (model.getGenres() != null && !model.getGenres().isEmpty()) {
            String queryInsertFilmGenre = "INSERT INTO FILMS_GENRES (FILM_ID, GENRE_ID) VALUES (:film_id, :genre_id);";
            MapSqlParameterSource [] genreFilmParams = new MapSqlParameterSource[model.getGenres().size()];
            int i = 0;
            for (Genre genre : model.getGenres()) {
                MapSqlParameterSource genreFilmParam = new MapSqlParameterSource();
                genreFilmParam.addValue("film_id", model.getId());
                genreFilmParam.addValue("genre_id", genre.getId());
                genreFilmParams[i++] = genreFilmParam;
            }
            jdbc.batchUpdate(queryInsertFilmGenre, genreFilmParams);
        }

        return model;
    }

    @Override
    public Film update(Film model) {
        String sqlUpdate = """
                    UPDATE FILMS SET NAME = :name, DESCRIPTION = :description, RELEASE_DATE = :release_date,
                                    MPA_ID = :mpa_id, DURATION = :duration
                    WHERE FILM_ID = :film_id;
                """;
        MapSqlParameterSource filmParams = new MapSqlParameterSource("film_id", model.getId());
        filmParams.addValue("name", model.getName());
        filmParams.addValue("description", model.getDescription());
        filmParams.addValue("release_date", model.getReleaseDate());
        filmParams.addValue("mpa_id", model.getMpa().getId());
        filmParams.addValue("duration", model.getDuration());
        int countUpdate = jdbc.update(sqlUpdate, filmParams);
        if (countUpdate == 0) {
            throw new NotFoundException("Film with id " + model.getId() + " not found");
        }

        MapSqlParameterSource filmParam = new MapSqlParameterSource("film_id", model.getId());
        String queryToDeleteFilmGenres = "DELETE FROM FILMS_GENRES WHERE FILM_ID = :film_id;";
        jdbc.update(queryToDeleteFilmGenres, filmParam);

        if (model.getGenres() != null && !model.getGenres().isEmpty()) {
            String queryForUpdateGenre = "INSERT INTO FILMS_GENRES (FILM_ID, GENRE_ID) VALUES (:film_id, :genre_id);";
            MapSqlParameterSource [] genreFilmParams = new MapSqlParameterSource[model.getGenres().size()];
            int i = 0;
            for (Genre genre : model.getGenres()) {
                MapSqlParameterSource genreFilmParam = new MapSqlParameterSource();
                genreFilmParam.addValue("film_id", model.getId());
                genreFilmParam.addValue("genre_id", genre.getId());
                genreFilmParams[i++] = genreFilmParam;
            }
            jdbc.batchUpdate(queryForUpdateGenre, genreFilmParams);
        }
        return model;
    }

    @Override
    public void addLike(Film film, User user) {
        String sqlQuery = "INSERT INTO films_likes (film_id, user_id) VALUES (:film_id, :user_id)";
        jdbc.update(sqlQuery, Map.of("film_id", film.getId(), "user_id", user.getId()));
    }

    @Override
    public void deleteLike(Film film, User user) {
        String sqlQuery = "DELETE FROM FILMS_LIKES WHERE FILM_ID = :film_id AND USER_ID = :user_id;";
        jdbc.update(sqlQuery, Map.of("film_id", film.getId(), "user_id", user.getId()));
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        String sql = """
                    SELECT FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, F.MPA_ID,
                    M.MPA_NAME, T.COUNT_LIKES
                    FROM FILMS F JOIN
                    (SELECT TOP_FILM_ID, COUNT_LIKES
                     FROM
                        (SELECT FILM_ID TOP_FILM_ID, COUNT(*) COUNT_LIKES
                         FROM FILMS_LIKES
                         GROUP BY FILM_ID
                         ORDER BY COUNT_LIKES DESC
                         )
                     LIMIT :count) T ON F.FILM_ID = T.TOP_FILM_ID
                     JOIN MPA M ON F.MPA_ID = M.MPA_ID;
                    """;
        Collection<Film> films = jdbc.query(sql, Map.of("count", count), rowMapper);
        String sqlGenre = """
                                SELECT g.genre_id, g.genre_name
                                FROM films_genres f JOIN genres g ON f.genre_id = g.genre_id
                                WHERE f.film_id = :film_id
                                ORDER BY G.GENRE_NAME
                          """;
        for (Film film : films) {
            MapSqlParameterSource genreParams = new MapSqlParameterSource("film_id", film.getId());
            film.setGenres(new LinkedHashSet<>(jdbc.query(sqlGenre, genreParams, genreRowMapper)));
        }
        return films;
    }
}