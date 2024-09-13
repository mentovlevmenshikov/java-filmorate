package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.validator.Update;
import ru.yandex.practicum.filmorate.service.FilmSearchBy;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.ValidationService;

import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController extends Controller<Film> {

    private final FilmService filmService;

    public FilmController(ValidationService<Film> validationService, FilmService filmService) {
        super(validationService, filmService);
        this.filmService = filmService;
    }

    @Override
    @GetMapping
    public Collection<Film> getAll() {
        log.info("Запрос всех фильмов");
        Collection<Film> allFilms = modelService.getAll();
        log.info("Возврат фильмов в кол-ве: {}", allFilms.size());
        return allFilms;
    }

    @Override
    @GetMapping("/{id}")
    public Film get(@PathVariable long id) {
        log.info("Запрос фильма с id: {}", id);
        Film film = modelService.get(id);
        log.info("Возврат фильма: {}", film);
        return film;
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@RequestBody @Valid Film film) {
        log.info("Создание фильма: {}", film);
        validationService.validate4Create(film);
        Film filmCreated = modelService.create(film);
        log.info("Создан фильм: {}", filmCreated);
        return filmCreated;
    }

    @Override
    @PutMapping
    public Film update(@RequestBody @Validated(Update.class) Film film) {
        log.info("Обновление фильма: {}", film);
        validationService.validate4Update(film);
        Film updated = modelService.update(film);
        log.info("Обновленный фильм: {}", updated);
        return updated;
    }

    @Override
    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        log.info("Удаление фильма с id: {}", id);
        filmService.delete(id);
        log.info("Фильм удален");
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Добавление лайка фильму с id {} пользователем {}", id, userId);
        int countLikes = filmService.addLike(id, userId);
        log.info("Кол-во лайков у фильма: {}", countLikes);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Удаление лайка у фильма с id {} пользователем {}", id, userId);
        int countLikes = filmService.deleteLike(id, userId);
        log.info("Кол-во лайков у фильма: {}", countLikes);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularByGenreIdAndYear(@RequestParam(defaultValue = "10") int count,
                                                       @RequestParam(required = false) Long genreId,
                                                       @RequestParam(required = false) Integer year) {
        log.info("Получение популярных фильмов c id жанра = {} и годом = {} в кол-ве: {}", genreId, year, count);
        Collection<Film> popularFilms = filmService.getPopularByGenreIdAndYear(count, genreId, year);
        log.info("Выбрано популярных фильмов в кол-ве: {}", popularFilms.size());
        return popularFilms;
    }

    @GetMapping("/director/{directorId}")
    public Collection<Film> getByDirector(@PathVariable long directorId, @RequestParam String sortBy) {
        log.info("Запрос фильмов по режиссеру с director_id: {}, сортировка {}", directorId, sortBy);
        Collection<Film> films = filmService.getByDirector(directorId, sortBy);
        log.info("Возврат фильмов: {}", films);
        return films;
    }

    @GetMapping("/search")
    public Collection<Film> getByQuery(@RequestParam String query, @RequestParam String by) {
        log.info("Поиск фильмов query = [{}]; by = [{}]", query, by);
        FilmSearchBy searchBy = getSearchBy(by);
        Collection<Film> films = filmService.getByQuery(searchBy, query);
        log.info("Найдено фильмов {}", films.size());
        return films;
    }

    private FilmSearchBy getSearchBy(String by) {
        if (by == null || by.isBlank()) {
            throw new ValidationException("Не указан способ поиска.");
        }

        String [] bys = by.split(",");

        try  {
            FilmSearchBy searchBy = FilmSearchBy.valueOf(bys[0].toUpperCase());
            if (bys.length == 2) {
                FilmSearchBy searchBy2 = FilmSearchBy.valueOf(bys[1].toUpperCase());
                if (searchBy != searchBy2) {
                    searchBy = FilmSearchBy.DIRECTOR_TITLE_BOTH;
                }
            }
            return  searchBy;
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Неизвестный способ поиска.");
        }
    }

    @GetMapping("/common")
    public Collection<Film> getCommonFilmsByUserIdAndFriendId(@RequestParam Long userId, @RequestParam Long friendId) {
        log.info("Запрос общих фильмов пользователя с id = {} и его друга с id = {}", userId, friendId);
        Collection<Film> films = filmService.getCommonFilmsByUserIdAndFriendId(userId, friendId);
        log.info("Возврат общих фильмов пользователя с id = {} и его друга с id = {}", userId, friendId);
        return films;
    }
}
