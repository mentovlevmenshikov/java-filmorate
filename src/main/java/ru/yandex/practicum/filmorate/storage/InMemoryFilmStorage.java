package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryFilmStorage extends InMemoryModelStorage<Film> implements LikeStorage {

    protected final Map<Film, Set<Long>> filmUserLikes = new HashMap<>();

    @Override
    public void addLike(Film film, User user) {
        Set<Long> filmLikeUserIds = filmUserLikes.computeIfAbsent(film, id -> new HashSet<>());
        if (filmLikeUserIds.add(user.getId())) {
            film.setCountLikes(filmLikeUserIds.size());
        }
    }

    @Override
    public void deleteLike(Film film, User user) {
        Set<Long> filmLikeUserIds = filmUserLikes.computeIfAbsent(film, id -> new HashSet<>());
        if (filmLikeUserIds.remove(user.getId())) {
            film.setCountLikes(filmLikeUserIds.size());
            if (film.getCountLikes() == 0) {
                filmUserLikes.remove(film.getId());
            }
        }
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        TreeSet<Film> filmsSortByLikes = new TreeSet<>(Comparator.comparing(Film::getCountLikes, Comparator.reverseOrder())
                .thenComparing(Film::getId));
        filmsSortByLikes.addAll(filmUserLikes.keySet());
        int countPopularFilm = count > filmsSortByLikes.size() ? filmsSortByLikes.size() : count;
        Collection<Film> popularFilms = new ArrayList<>(countPopularFilm);
        Iterator<Film> itr = filmsSortByLikes.iterator();
        while (itr.hasNext()) {
            Film element = itr.next();
            popularFilms.add(element);
            if (popularFilms.size() == countPopularFilm) {
                break;
            }
        }
        return popularFilms;
    }
}
