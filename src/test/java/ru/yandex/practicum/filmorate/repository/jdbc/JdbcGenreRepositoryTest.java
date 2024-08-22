package ru.yandex.practicum.filmorate.repository.jdbc;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatList;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class JdbcGenreRepositoryTest {
    private final JdbcGenreRepository genreRepository;

    @Test
    void getAllGenres() {
        List<Genre> listOfGenres = (List<Genre>) genreRepository.getAll();
        assertThatList(listOfGenres).hasSizeBetween(6, 6);
        assertThat(listOfGenres.getFirst()).isEqualTo(Genre.builder().id(1L).name("Комедия").build());
    }

    @Test
    void getGenreById() {
        Genre expected = Genre.builder().id(2L).name("Драма").build();
        assertThat(genreRepository.getById(2).get()).isEqualTo(expected);
    }
}