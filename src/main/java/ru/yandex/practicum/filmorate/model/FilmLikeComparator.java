package ru.yandex.practicum.filmorate.model;

import java.util.Comparator;

public class FilmLikeComparator implements Comparator<Film> {
    @Override
    public int compare(Film film1, Film film2) {
        return film1.getCountLikes() - film2.getCountLikes();
    }
}
