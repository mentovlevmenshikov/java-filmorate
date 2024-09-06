package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.validator.Update;
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
    public Collection<Film> getPopular(@RequestParam(defaultValue = "10") int count) {
        log.info("Получение популярных фильмов в кол-ве: {}", count);
        Collection<Film> popularFilms = filmService.getPopular(count);
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
}
