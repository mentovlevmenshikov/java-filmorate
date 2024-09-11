package ru.yandex.practicum.filmorate.repository.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import org.springframework.dao.DuplicateKeyException;

import java.util.*;

@Repository
@Slf4j
public class JdbcFilmRepository extends JdbcBaseRepository<Film> implements FilmRepository {

    private final RowMapper<Genre> genreRowMapper;
    private final ResultSetExtractor<Map<Long, LinkedHashSet<Genre>>> filmsGenresExtractor;
    private final RowMapper<Director> directorRowMapper;
    private final ResultSetExtractor<Map<Long, HashSet<Director>>> filmsDirectorsExtractor;

    public JdbcFilmRepository(RowMapper<Film> filmRowMapper, RowMapper<Genre> genreRowMapper,
                              RowMapper<Director> directorRowMapper,
                              ResultSetExtractor<Map<Long, LinkedHashSet<Genre>>> filmsGenresExtractor,
                              ResultSetExtractor<Map<Long, HashSet<Director>>> filmsDirectorsExtractor) {
        super(filmRowMapper);
        this.filmsGenresExtractor = filmsGenresExtractor;
        this.genreRowMapper = genreRowMapper;
        this.directorRowMapper = directorRowMapper;
        this.filmsDirectorsExtractor = filmsDirectorsExtractor;
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

        String sqlDirectors = """
                      SELECT d.director_id, d.director_name
                      FROM films_directors f JOIN directors d ON f.director_id = d.director_id
                      WHERE f.film_id = :film_id
                      ORDER BY d.director_id
                """;
        MapSqlParameterSource directorParams = new MapSqlParameterSource("film_id", Objects.requireNonNull(film).getId());
        film.setDirectors(new HashSet<>(jdbc.query(sqlDirectors, directorParams, directorRowMapper)));

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
        fillReferenceFields(films);
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

        insertGenres(model);
        insertDirectors(model);

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
        insertGenres(model);

        String queryToDeleteFilmDirectors = "DELETE FROM FILMS_DIRECTORS WHERE FILM_ID = :film_id;";
        jdbc.update(queryToDeleteFilmDirectors, filmParam);
        insertDirectors(model);


        return model;
    }

    @Override
    public void delete(long id) {
        String sqlDelete = "DELETE FROM films WHERE film_id = :id";
        MapSqlParameterSource filmParams = new MapSqlParameterSource("id", id);
        try {
            jdbc.update(sqlDelete, filmParams);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Film with id {} not found", id);
        }
    }

    @Override
    public void addLike(Film film, User user) {
        String sqlQuery = "INSERT INTO films_likes (film_id, user_id) VALUES (:film_id, :user_id)";
        try {
            jdbc.update(sqlQuery, Map.of("film_id", film.getId(), "user_id", user.getId()));
        } catch (DuplicateKeyException exception) {
            log.warn("User {} already liked film {}", user.getId(), film.getId());
        }
    }

    @Override
    public void deleteLike(Film film, User user) {
        String sqlQuery = "DELETE FROM FILMS_LIKES WHERE FILM_ID = :film_id AND USER_ID = :user_id;";
        jdbc.update(sqlQuery, Map.of("film_id", film.getId(), "user_id", user.getId()));
    }

    public Collection<Film> getPopularByGenreIdAndYear(int count, Long genreId, Integer year) {

        MapSqlParameterSource filmsParams = new MapSqlParameterSource();
        filmsParams.addValue("count", count);
        //filmsParams.addValue("year", year != null ? year : "%");
        filmsParams.addValue("year", year);
        filmsParams.addValue("genre_id", genreId);

        String sql = """
                SELECT FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, F.MPA_ID,
                                    M.MPA_NAME, T.COUNT_LIKES
                                    FROM FILMS F LEFT JOIN
                                    (SELECT TOP_FILM_ID, COUNT_LIKES
                                     FROM
                                        (SELECT ff.FILM_ID TOP_FILM_ID, COUNT(*) COUNT_LIKES
                                         FROM FILMS ff
                                         JOIN FILMS_LIKES fl ON fl.FILM_ID = ff.FILM_ID
                                         JOIN FILMS_GENRES fg ON fl.FILM_ID = fg.FILM_ID AND fg.GENRE_ID = :genre_id OR :genre_id IS NULL
                                         WHERE (EXTRACT(YEAR FROM ff.RELEASE_DATE) = :year OR :year IS NULL)
                                         GROUP BY ff.FILM_ID
                                         ORDER BY COUNT_LIKES DESC
                                         )
                                     ) T ON F.FILM_ID = T.TOP_FILM_ID
                                     JOIN MPA M ON F.MPA_ID = M.MPA_ID
                                     WHERE (:genre_id IS NULL OR :genre_id IS NOT NULL AND COUNT_LIKES IS NOT NULL)
                                     AND (:year IS NULL OR :year IS NOT NULL AND COUNT_LIKES IS NOT NULL)
                                     ORDER BY COUNT_LIKES DESC
                                     LIMIT :count;
                """;
        Collection<Film> films = jdbc.query(sql, filmsParams, rowMapper);
        fillReferenceFields(films);
        return films;
    }

    @Override
    public void getGenresOfFilms(Collection<Film> films) {
        String sqlFilmsGenres = """
                SELECT f.film_id, g.genre_id, g.genre_name
                FROM films_genres f JOIN genres g ON f.genre_id = g.genre_id
                ORDER BY g.genre_id
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
    }

    @Override
    public void getDirectorsOfFilms(Collection<Film> films) {
        String sqlFilmsDirectors = """
                SELECT f.film_id, d.director_id, d.director_name
                FROM films_directors f JOIN directors d ON f.director_id = d.director_id
                ORDER BY d.director_id
                """;
        Map<Long, HashSet<Director>> filmsDirectors = jdbc.query(sqlFilmsDirectors, filmsDirectorsExtractor);
        if (filmsDirectors != null && !filmsDirectors.isEmpty()) {
            films.forEach(film -> {
                HashSet<Director> directors = filmsDirectors.get(film.getId());
                if (directors != null) {
                    film.setDirectors(directors);
                }
            });
        }
    }

    @Override
    public Collection<Film> getFilmsByDirector(long directorId, String sortBy) {
        String sqlFilms = """
                SELECT f.film_Id, f.name, f.description, f.release_date, f.duration,
                       m.mpa_id, m.mpa_name,
                       (SELECT count(*) FROM films_likes L WHERE L.film_id = f.film_id) count_likes
                FROM films_directors fd
                JOIN films f ON fd.film_id = f.film_id
                JOIN mpa m ON f.mpa_id = m.mpa_id
                WHERE fd.director_id = :directorId
                """;

        if (sortBy.equals("year")) {
            sqlFilms = sqlFilms + "ORDER BY f.release_date;";
        } else if (sortBy.equals("likes")) {
            sqlFilms = sqlFilms + "ORDER BY count_likes DESC;";
        }

        Collection<Film> films = jdbc.query(sqlFilms, Map.of("directorId", directorId), rowMapper);
        fillReferenceFields(films);
        return films;
    }

    @Override
    public void insertGenres(Film film) {
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String query = "INSERT INTO FILMS_GENRES (FILM_ID, GENRE_ID) VALUES (:film_id, :genre_id);";
            MapSqlParameterSource[] genreFilmParams = new MapSqlParameterSource[film.getGenres().size()];
            int i = 0;
            for (Genre genre : film.getGenres()) {
                MapSqlParameterSource genreFilmParam = new MapSqlParameterSource();
                genreFilmParam.addValue("film_id", film.getId());
                genreFilmParam.addValue("genre_id", genre.getId());
                genreFilmParams[i++] = genreFilmParam;
            }
            jdbc.batchUpdate(query, genreFilmParams);
        } else {
            film.setGenres(new LinkedHashSet<>());
        }
    }

    @Override
    public void insertDirectors(Film film) {
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            String query = "INSERT INTO FILMS_DIRECTORS (FILM_ID, DIRECTOR_ID) VALUES (:film_id, :director_id);";
            MapSqlParameterSource[] directorFilmParams = new MapSqlParameterSource[film.getDirectors().size()];
            int i = 0;
            for (Director director : film.getDirectors()) {
                MapSqlParameterSource directorFilmParam = new MapSqlParameterSource();
                directorFilmParam.addValue("film_id", film.getId());
                directorFilmParam.addValue("director_id", director.getId());
                directorFilmParams[i++] = directorFilmParam;
            }
            jdbc.batchUpdate(query, directorFilmParams);
        } else {
            film.setDirectors(new HashSet<>());
        }
    }

    @Override
    public Collection<Film> searchByDirector(String query) {
        String sql = """
                SELECT f.film_Id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.mpa_name,
                       (SELECT count(*) FROM FILMS_LIKES L WHERE L.FILM_ID = f.FILM_ID) count_likes
                FROM directors d JOIN films_directors fd ON fd.director_id = d.director_id
                JOIN films f ON f.film_id = fd.film_id
                JOIN MPA m ON f.mpa_id = m.mpa_id
                WHERE LOWER(d.director_name) LIKE LOWER(:query)
                ORDER BY count_likes desc
                """;
        Collection<Film> films = jdbc.query(sql, Map.of("query", formatQuery(query)), rowMapper);
        fillReferenceFields(films);
        return films;
    }

    @Override
    public Collection<Film> searchByTitle(String query) {
        String sql = """
                SELECT f.film_Id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.mpa_name,
                       (SELECT count(*) FROM FILMS_LIKES L WHERE L.FILM_ID = f.FILM_ID) count_likes
                FROM films f
                JOIN MPA m ON f.mpa_id = m.mpa_id
                WHERE LOWER(f.name) LIKE LOWER(:query)
                ORDER BY count_likes desc
                """;
        Collection<Film> films = jdbc.query(sql, Map.of("query", formatQuery(query)), rowMapper);
        fillReferenceFields(films);
        return films;
    }

    @Override
    public Collection<Film> searchByDirectorTitleBoth(String query) {
        String sql = """
                SELECT *
                FROM (
                        SELECT f.film_Id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.mpa_name,
                               (SELECT count(*) FROM FILMS_LIKES L WHERE L.FILM_ID = f.FILM_ID) count_likes
                        FROM films f JOIN MPA m ON f.mpa_id = m.mpa_id
                        WHERE LOWER(f.name) LIKE LOWER(:query)
                        union
                        SELECT f.film_Id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.mpa_name,
                               (SELECT count(*) FROM FILMS_LIKES L WHERE L.FILM_ID = f.FILM_ID) count_likes
                        FROM directors d JOIN films_directors fd ON fd.director_id = d.director_id
                        JOIN films f ON f.film_id = fd.film_id
                        JOIN MPA m ON f.mpa_id = m.mpa_id
                        WHERE LOWER(d.director_name) LIKE LOWER(:query)
                    )
                ORDER BY count_likes desc
                """;
        Collection<Film> films = jdbc.query(sql, Map.of("query", formatQuery(query)), rowMapper);
        fillReferenceFields(films);
        return films;
    }

    private void fillReferenceFields(Collection<Film> films) {
        if (!films.isEmpty()) {
            getGenresOfFilms(films);
            getDirectorsOfFilms(films);
        }
    }

    private String formatQuery(String query) {
        return "%" + query + "%";
    }

    public Collection<Film> getCommonFilmsByUserIdAndFriendId(Long userId, Long friendId) {
        MapSqlParameterSource filmsParams = new MapSqlParameterSource();
        filmsParams.addValue("user_id", userId);
        filmsParams.addValue("friend_id", friendId);

        String sql = """
                SELECT FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, F.MPA_ID,
                                                    M.MPA_NAME, T.COUNT_LIKES
                                                    FROM FILMS F JOIN (
                                                         SELECT fl.FILM_ID TOP_FILM_ID, COUNT(*) COUNT_LIKES
                                                         FROM FILMS_LIKES fl
                                                         WHERE USER_ID IN (:user_id, :friend_id)
                                                         GROUP BY fl.FILM_ID
                                                         HAVING COUNT(*) = 2
                                                         ORDER BY COUNT_LIKES DESC
                                                         ) T ON F.FILM_ID = T.TOP_FILM_ID
                                                     JOIN MPA M ON F.MPA_ID = M.MPA_ID;
                    """;
        Collection<Film> films = jdbc.query(sql, filmsParams, rowMapper);

        if (films.isEmpty()) {
            return Collections.emptyList();
        }

        getGenresOfFilms(films);
        getDirectorsOfFilms(films);

        return films;
    }

    @Override
    public Set<Film> getLikedFilmsByUser(long userId) {
        String sqlQuery = """
        SELECT f.film_Id, f.name, f.description, f.release_date, f.duration,
         m.mpa_id, m.mpa_name, (SELECT count(*) FROM films_likes l WHERE l.film_id = f.film_id) count_likes
         FROM films f
         JOIN films_likes fl ON f.film_Id = fl.film_Id
         JOIN mpa m ON f.mpa_id = m.mpa_id
         WHERE fl.user_id = :userId
         """;

        Collection<Film> likedFilms = jdbc.query(sqlQuery, Map.of("userId", userId), rowMapper);

        getGenresOfFilms(likedFilms);
        getDirectorsOfFilms(likedFilms);

        return new HashSet<>(likedFilms);
    }
}