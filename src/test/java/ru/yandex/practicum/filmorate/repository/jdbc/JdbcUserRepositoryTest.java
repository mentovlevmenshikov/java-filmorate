package ru.yandex.practicum.filmorate.repository.jdbc;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatList;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = "classpath:TestUserData.sql")
class JdbcUserRepositoryTest {
    private final JdbcUserRepository userRepository;

    @Test
    public void shouldGetAllUsers() {
        List<User> userList = (List<User>) userRepository.getAll();
        assertThatList(userList).hasSizeBetween(4, 4);
    }

    @Test
    public void shouldGetListOfFriends() {
        List<User> userList = (List<User>) userRepository.getAll();
        User testUser = userList.get(0);
        User testFriend1 = userList.get(1);
        User testFriend2 = userList.get(2);
        userRepository.addFriend(testUser, testFriend1);
        userRepository.addFriend(testUser, testFriend2);
        List<User> friends = (List<User>) userRepository.getFriends(testUser.getId());
        assertThat(friends.get(0)).isEqualTo(testFriend1);
        assertThat(friends.get(1)).isEqualTo(testFriend2);
    }

    @Test
    public void shouldAddFriend() {
        List<User> userList = (List<User>) userRepository.getAll();
        User testUser = userList.get(0);
        User testFriend1 = userList.get(1);
        userRepository.addFriend(testUser, testFriend1);
        List<User> friends = (List<User>) userRepository.getFriends(testUser.getId());
        assertThat(friends.getFirst()).isEqualTo(testFriend1);
    }

    @Test
    public void shouldDeleteFriend() {
        List<User> userList = (List<User>) userRepository.getAll();
        User testUser = userList.get(0);
        User testFriend1 = userList.get(1);
        userRepository.addFriend(testUser, testFriend1);
        List<User> friends = (List<User>) userRepository.getFriends(testUser.getId());
        assertThat(friends.getFirst()).isEqualTo(testFriend1);
        userRepository.deleteFriend(testUser, testFriend1);
        List<User> friendsAfterDelete = (List<User>) userRepository.getFriends(testUser.getId());
        assertThatList(friendsAfterDelete).hasSizeBetween(0, 0);
    }

    @Test
    public void shouldGetListOfCommonFriends() {
        List<User> userList = (List<User>) userRepository.getAll();
        User testUser1 = userList.get(0);
        User testUser2 = userList.get(1);
        User testFriend1 = userList.get(2);
        User testFriend2 = userList.get(3);
        userRepository.addFriend(testUser1, testFriend1);
        userRepository.addFriend(testUser1, testFriend2);
        userRepository.addFriend(testUser2, testFriend1);
        userRepository.addFriend(testUser2, testFriend2);
        List<User> commonFriends = (List<User>) userRepository.getCommonFriends(testUser1.getId(), testUser2.getId());
        assertThat(commonFriends.get(0)).isEqualTo(testFriend1);
        assertThat(commonFriends.get(1)).isEqualTo(testFriend2);
    }
}