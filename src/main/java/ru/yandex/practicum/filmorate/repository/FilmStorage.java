package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> getFilmsByDirector(long directorId, String sortBy);

    void getGenresOfFilms(Collection<Film> films);

    void getDirectorsOfFilms(Collection<Film> films);

    void insertGenres(Film film);

    void insertDirectors(Film film);
}
