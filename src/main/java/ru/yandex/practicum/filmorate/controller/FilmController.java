package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.ValidationService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private final ValidationService<Film> validationService;
    private long counterId = 0;


    public FilmController(ValidationService<Film> validationService) {
        this.validationService = validationService;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Запрос всех фильмов");
        Collection<Film> allFilms = films.values();
        log.info("Возврат фильмов в кол-ве: {}", allFilms.size());
        return allFilms;
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Создание фильма: {}", film);
        validationService.validate4Create(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Создан фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        log.info("Обновление фильма: {}", film);
        validationService.validate4Update(film);
        Film film4Update = find(film);
        film4Update.setName(film.getName());
        film4Update.setDescription(film.getDescription());
        film4Update.setReleaseDate(film.getReleaseDate());
        film4Update.setDuration(film.getDuration());
        log.info("Обновленный фильм: {}", film4Update);
        return film4Update;
    }

    private Film find(Film film) {
       Film findedFilm = films.get(film.getId());
       if (findedFilm == null) {
           throw new NotFoundException(String.format("Фильм с id = %s не найден.", film.getId()));
       }
       return findedFilm;
    }

    private long getNextId() {
        return ++counterId;
    }
}
