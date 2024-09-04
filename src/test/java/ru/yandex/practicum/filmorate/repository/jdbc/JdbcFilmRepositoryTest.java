package ru.yandex.practicum.filmorate.repository.jdbc;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = "classpath:TestFilmData.sql")
class JdbcFilmRepositoryTest {
    private final JdbcFilmRepository filmRepository;

    @Test
    void shouldGetAllFilms() {
        List<Film> filmsList = (List<Film>) filmRepository.getAll();
        assertThatList(filmsList).hasSizeBetween(4, 4);
    }

    @Test
    void shouldAddLike() {
        filmRepository.addLike(Film.builder().id(3L).build(), User.builder().id(1L).build());
        filmRepository.addLike(Film.builder().id(3L).build(), User.builder().id(2L).build());
        List<Film> popularFilms = (List<Film>) filmRepository.getPopularFilms(4);
        Film expected = filmRepository.getById(3).get();
        Film actual = popularFilms.getFirst();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldDeleteLike() {
        filmRepository.addLike(Film.builder().id(3L).build(), User.builder().id(1L).build());
        filmRepository.addLike(Film.builder().id(3L).build(), User.builder().id(2L).build());
        filmRepository.deleteLike(Film.builder().id(3L).build(), User.builder().id(1L).build());
        List<Film> popularFilms = (List<Film>) filmRepository.getPopularFilms(4);
        int likes_count = popularFilms.stream().filter(film -> film.getId().equals(3L)).map(Film::getCountLikes).findFirst().get();
        assertEquals(1, likes_count);
        filmRepository.deleteLike(Film.builder().id(3L).build(), User.builder().id(2L).build());
        popularFilms = (List<Film>) filmRepository.getPopularFilms(4);
        likes_count = popularFilms.stream().filter(film -> film.getId().equals(3L)).map(Film::getCountLikes).findFirst().get();
        assertEquals(0, likes_count);
    }

    @Test
    void shouldGetPopularFilms() {
        filmRepository.addLike(Film.builder().id(1L).build(), User.builder().id(1L).build());
        filmRepository.addLike(Film.builder().id(2L).build(), User.builder().id(2L).build());
        filmRepository.addLike(Film.builder().id(3L).build(), User.builder().id(1L).build());
        List<Film> popularFilms = (List<Film>) filmRepository.getPopularFilms(4);
        System.out.println(popularFilms.toString());
        Film actual1 = popularFilms.get(0);
        Film actual2 = popularFilms.get(1);
        Film actual3 = popularFilms.get(2);
        assertThat(actual1).isEqualTo(filmRepository.getById(1).get());
        assertThat(actual2).isEqualTo(filmRepository.getById(2).get());
        assertThat(actual3).isEqualTo(filmRepository.getById(3).get());
        assertThatList(popularFilms).hasSizeBetween(4, 4);
    }
}