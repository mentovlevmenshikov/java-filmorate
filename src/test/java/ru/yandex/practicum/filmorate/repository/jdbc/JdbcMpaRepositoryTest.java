package ru.yandex.practicum.filmorate.repository.jdbc;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.ListAssert.assertThatList;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class JdbcMpaRepositoryTest {
    private final JdbcMpaRepository mpaRepository;

    @Test
    void shouldGetAllMpaRatings() {
        List<MPA> listOfMpa = (List<MPA>)mpaRepository.getAll();
        assertThatList(listOfMpa).hasSizeBetween(5, 5);
        assertThat(listOfMpa.getFirst()).isEqualTo(MPA.builder().id(1L).name("G").build());
    }

    @Test
    void shouldGetMpaRatingById() {
        MPA actual = mpaRepository.getById(2L).get();
        assertThat(actual).isEqualTo(MPA.builder().id(2L).name("PG")
                .build());
    }
}