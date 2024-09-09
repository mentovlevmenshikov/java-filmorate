package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmRepository extends ModelRepository<Film>, DeleteStorage, LikeStorage {
    Collection<Film> getFilmsByDirector(long directorId, String sortBy);

    void getGenresOfFilms(Collection<Film> films);

    void getDirectorsOfFilms(Collection<Film> films);

    void insertGenres(Film film);

    void insertDirectors(Film film);

    Collection<Film> searchByDirector(String query );

    Collection<Film> searchByTitle(String query);

    Collection<Film> searchByDirectorTitleBoth(String query);
}
