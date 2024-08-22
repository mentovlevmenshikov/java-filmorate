package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.validator.Update;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public Collection<Genre> getAll() {
        log.info("Запрос всех Genre");
        Collection<Genre> allGenre = genreService.getAll();
        log.info("Возврат Genre в кол-ве: {}", allGenre.size());
        return allGenre;
    }

    @GetMapping("/{id}")
    public Genre getById(@NotNull @PathVariable long id) {
        log.info("Запрос Genre с id: {}", id);
        Genre genre = genreService.get(id);
        log.info("Возврат Genre: {}", genre);
        return genre;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Genre create(@RequestBody @Valid Genre genre) {
        log.info("Создание Genre: {}", genre);
        Genre created = genreService.create(genre);
        log.info("Создан Genre: {}", genre);
        return created;
    }

    @PutMapping
    public Genre update(@RequestBody @Validated(Update.class) Genre genre) {
        log.info("Обновление Genre: {}", genre);
        Genre updated = genreService.update(genre);
        log.info("Обновленный Genre: {}", genre);
        return updated;
    }
}
