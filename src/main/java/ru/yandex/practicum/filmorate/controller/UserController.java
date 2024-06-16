package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidationService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private final ValidationService<User> validationService;
    private long counterId = 0;

    public UserController(ValidationService<User> validationService) {
        this.validationService = validationService;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Запрос всех пользователей");
        Collection<User> allUsers = users.values();
        log.info("Возврат пользователей в кол-ве: {}", allUsers.size());
        return allUsers;
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.info("Создание пользователя: {}", user);
        preSetFields(user);
        validationService.validate4Create(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Создан пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        log.info("Обновление пользователя: {}", user);
        preSetFields(user);
        validationService.validate4Update(user);
        User user4Update = find(user);
        user4Update.setName(user.getName());
        user4Update.setEmail(user.getEmail());
        user4Update.setLogin(user.getLogin());
        user4Update.setBirthday(user.getBirthday());
        log.info("Обновленный пользователь: {}", user4Update);
        return user4Update;
    }

    private User find(User user) {
        User findedUser = users.get(user.getId());
        if (findedUser == null) {
            throw new NotFoundException(String.format("Пользователь с id = %s не найден.", user.getId()));
        }

        return findedUser;
    }

    private long getNextId() {
        return ++counterId;
    }

    private void preSetFields(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
