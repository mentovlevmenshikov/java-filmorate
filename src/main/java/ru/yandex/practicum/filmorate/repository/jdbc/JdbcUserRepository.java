package ru.yandex.practicum.filmorate.repository.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FriendStorage;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
@Slf4j
public class JdbcUserRepository extends JdbcBaseRepository<User> implements FriendStorage  {

    public JdbcUserRepository(RowMapper<User> userRowMapper) {
        super(userRowMapper);
    }

    @Override
    public Optional<User> getById(long id) {
        String sql = "SELECT user_id, email, login, name, birthday FROM users WHERE user_id = :id";
        User user;
        try {
            user = jdbc.queryForObject(sql, Map.of("id", id), rowMapper);
        } catch (EmptyResultDataAccessException e) {
            log.warn("User with id {} not found", id);
            return Optional.empty();
        }

        return Optional.ofNullable(user);
    }

    @Override
    public Collection<User> getAll() {
        String sql = "SELECT user_id, email, login, name, birthday FROM users";
        return jdbc.query(sql, rowMapper);
    }

    @Override
    public User create(User model) {
        String sqlInsert = "INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY) VALUES (:email, :login, :name, :birthday)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("email", model.getEmail());
        params.addValue("login", model.getLogin());
        params.addValue("name", model.getName());
        params.addValue("birthday", model.getBirthday());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sqlInsert, params, keyHolder);
        model.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return model;
    }

    @Override
    public User update(User model) {
        String sqlUpdate = """
            UPDATE USERS
            SET EMAIL = :email, LOGIN = :login, NAME = :name, BIRTHDAY = :birthday
            WHERE user_id = :id
            """;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", model.getId());
        params.addValue("email", model.getEmail());
        params.addValue("login", model.getLogin());
        params.addValue("name", model.getName());
        params.addValue("birthday", model.getBirthday());
        int countUpdate = jdbc.update(sqlUpdate,params);
        if (countUpdate == 0) {
            throw new NotFoundException("User with id " + model.getId() + " not found");
        }
        return model;
    }

    @Override
    public void addFriend(User user, User friend) {
        String sql = "INSERT INTO friends (user_id, friend_id, status) VALUES (:user_id, :friend_id, :status);";
        jdbc.update(sql, Map.of("user_id", user.getId(), "friend_id", friend.getId(), "status", 0));
    }

    @Override
    public void deleteFriend(User user, User friend) {
        String sql = "DELETE FROM friends WHERE user_id = :user_id AND friend_id = :friend_id";
        jdbc.update(sql, Map.of("user_id", user.getId(), "friend_id", friend.getId()));
    }

    @Override
    public Collection<User> getFriends(long userId) {
        String sql = "SELECT u.USER_ID, u.EMAIL, u.LOGIN, u.NAME, u.BIRTHDAY " +
                "FROM friends f JOIN users u ON f.friend_id = u.user_id WHERE f.user_id = :user_id";
        return jdbc.query(sql, Map.of("user_id", userId), rowMapper);

    }

    @Override
    public Collection<User> getCommonFriends(long userId, long otherUserId) {
        String sql = """
                SELECT u.USER_ID, u.EMAIL, u.LOGIN, u.NAME, u.BIRTHDAY
                FROM friends f2 JOIN friends f1 ON f1.friend_id = f2.friend_id
                                JOIN users u ON f1.friend_id = u.user_id
                WHERE f1.user_id = :user_id and f2.user_id = :other_user_id
                """;
        return jdbc.query(sql, Map.of("user_id", userId, "other_user_id", otherUserId), rowMapper);
    }
}
