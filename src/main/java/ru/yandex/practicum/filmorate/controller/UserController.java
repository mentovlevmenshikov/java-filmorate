package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserEvent;
import ru.yandex.practicum.filmorate.service.EventFeedService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.ValidationService;

import java.util.Collection;


@RestController
@RequestMapping("/users")
@Slf4j
public class UserController extends Controller<User> {
    private final UserService userService;
    private final EventFeedService eventFeedService;

    public UserController(ValidationService<User> validationService, UserService userService, EventFeedService eventFeedService) {
        super(validationService, userService);
        this.userService = userService;
        this.eventFeedService = eventFeedService;
    }

    @Override
    @GetMapping
    public Collection<User> getAll() {
        log.info("Запрос всех пользователей");
        Collection<User> allUsers = modelService.getAll();
        log.info("Возврат пользователей в кол-ве: {}", allUsers.size());
        return allUsers;
    }

    @Override
    @GetMapping("/{id}")
    public User get(@PathVariable long id) {
        log.info("Запрос пользователя с id: {}", id);
        User user = modelService.get(id);
        log.info("Возврат пользователя: {}", user);
        return user;
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody User user) {
        log.info("Создание пользователя: {}", user);
        preSetFields(user);
        validationService.validate4Create(user);
        User created = modelService.create(user);
        log.info("Создан пользователь: {}", created);
        return created;
    }

    @Override
    @PutMapping
    public User update(@RequestBody User user) {
        log.info("Обновление пользователя: {}", user);
        preSetFields(user);
        validationService.validate4Update(user);
        User updated = modelService.update(user);
        log.info("Обновленный пользователь: {}", updated);
        return updated;
    }

    @Override
    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        log.info("Удаление пользователя с id: {}", id);
        userService.delete(id);
        log.info("Пользователь удален");
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Добавление пользователю с id {} друга c id {}", id, friendId);
        userService.addFriend(id, friendId);
        log.info("Друг успешно добавлен");
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Удаление у пользователя с id {} друга с id {}", id, friendId);
        userService.deleteFriend(id, friendId);
        log.info("Друг успешно удален");
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable long id) {
        log.info("Запрос всех друзей у пользователя с id {}", id);
        Collection<User> friends = userService.getFriends(id);
        log.info("Возврат друзей в кол-ве: {}", friends.size());
        return friends;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.info("Запрос совпадающих друзей у пользователя с id {} и у пользователя с otherId {}", id, otherId);
        Collection<User> commonFriends = userService.getCommonFriends(id, otherId);
        log.info("Возврат общих друзей в кол-ве: {}", commonFriends.size());
        return commonFriends;
    }

    @GetMapping("/{id}/feed")
    public Collection<UserEvent> getEventFeed(@PathVariable long id) {
        log.info("Запрос ленты событи по пользователю с id {}", id);
        Collection<UserEvent> eventFeed = eventFeedService.getEventFeed(id);
        log.info("Возврат ленты событий с кол-вом событий {}", eventFeed.size());
        return eventFeed;
    }

    private void preSetFields(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
