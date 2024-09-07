package ru.yandex.practicum.filmorate.repository.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.DeleteStorage;
import ru.yandex.practicum.filmorate.repository.ReviewLikeStorage;

import java.util.*;

@Repository
@Slf4j
public class JdbcReviewRepository extends JdbcBaseRepository<Review> implements ReviewLikeStorage, DeleteStorage {
    public JdbcReviewRepository(RowMapper<Review> reviewRowMapper) {
        super(reviewRowMapper);
    }

    @Override
    public Collection<Review> getAll() {
        String sqlReview = "SELECT review_id, r.user_id, r.film_id, r.content, r.isPositive," +
                " (SELECT SUM(like_Dislike) FROM REVIEWS_USERS AS RU WHERE RU.review_id = r.review_id) count_likes" +
                " FROM reviews AS r";

        List<Review> reviews = jdbc.query(sqlReview, rowMapper);
        if (reviews.isEmpty()) {
            return Collections.emptyList();
        }
        return reviews;
    }

    @Override
    public Optional<Review> getById(long id) {
        String sqlReview = "SELECT r.review_id, r.user_id, r.film_id, r.content, r.isPositive," +
                " (SELECT SUM(like_Dislike) FROM REVIEWS_USERS AS RU WHERE RU.review_id = r.review_id) count_likes" +
                " FROM reviews AS r " +
                " WHERE r.review_id = :review_id";
        MapSqlParameterSource reviewParameterSource = new MapSqlParameterSource();
        reviewParameterSource.addValue("review_id", id);
        Review review;

        try {
            review = jdbc.queryForObject(sqlReview, reviewParameterSource, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Film with id {} not found", id);
            return Optional.empty();
        }
        return Optional.of(review);
    }

    @Override
    public Review create(Review review) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sqlInsert = "INSERT INTO REVIEWS (content, isPositive, user_id, film_id) " +
                           "VALUES (:content, :isPositive, :user_id, :film_id);";

        MapSqlParameterSource reviewParams = new MapSqlParameterSource();
        reviewParams.addValue("film_id", review.getFilmId());
        reviewParams.addValue("user_id", review.getUserId());
        reviewParams.addValue("content", review.getContent());
        reviewParams.addValue("isPositive", review.getIsPositive());

        jdbc.update(sqlInsert, reviewParams, keyHolder);
        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        return review;
    }

    @Override
    public Review update(Review review) {
        String sqlUpdate = " UPDATE REVIEWS SET CONTENT = :content, ISPOSITIVE = :isPositive, " +
                " USER_ID = :user_id, FILM_ID = :film_id" +
                " WHERE REVIEW_ID = :review_id;";

        MapSqlParameterSource reviewParams = new MapSqlParameterSource();
        reviewParams.addValue("review_id", review.getReviewId());
        reviewParams.addValue("film_id", review.getFilmId());
        reviewParams.addValue("user_id", review.getUserId());
        reviewParams.addValue("content", review.getContent());
        reviewParams.addValue("isPositive", review.getIsPositive());

        jdbc.update(sqlUpdate, reviewParams);

        return review;
    }
    @Override
    public void delete(long id) {
        String sqlDelete = "DELETE FROM REVIEWS WHERE REVIEW_ID = :review_id; ";

        MapSqlParameterSource reviewParams = new MapSqlParameterSource();
        reviewParams.addValue("review_id", id);

        jdbc.update(sqlDelete, reviewParams);
        this.deleteReviewUsers(id);
    }

    private void deleteReviewUsers(Long  id) {
        String sqlDelete = "DELETE FROM reviews_users WHERE REVIEW_ID = :review_id; ";

        MapSqlParameterSource reviewParams = new MapSqlParameterSource();
        reviewParams.addValue("review_id", id);
        jdbc.update(sqlDelete, reviewParams);
    }

    @Override
    public void addLike(Review review, User user) {
        String sqlQuery = "INSERT INTO reviews_users (review_id, user_id, like_Dislike) VALUES (:review_id, :user_id, :like)";

        MapSqlParameterSource likeParams = new MapSqlParameterSource();
        likeParams.addValue("review_id", review.getReviewId());
        likeParams.addValue("user_id", user.getId());
        likeParams.addValue("like", 1);

        if (checkExistReviewLike(review.getReviewId(),user.getId())) {
            deleteLike(review,user);
        }
        jdbc.update(sqlQuery,likeParams);
    }
    @Override
    public void addDisLike(Review review, User user) {
        String sqlQuery = "INSERT INTO reviews_users (review_id, user_id, like_Dislike) " +
                          "VALUES (:review_id, :user_id, :dislike)";

        MapSqlParameterSource likeParams = new MapSqlParameterSource();
        likeParams.addValue("review_id", review.getReviewId());
        likeParams.addValue("user_id", user.getId());
        likeParams.addValue("dislike", -1);

        if (checkExistReviewLike(review.getReviewId(),user.getId())) {
            deleteLike(review,user);
        }
        jdbc.update(sqlQuery, likeParams);
    }
    private boolean checkExistReviewLike(Long reviewId, Long userId) {
        boolean ret = false;
        final String sqlQuery = "SELECT count(*) FROM reviews_users WHERE review_id = :review_id AND user_id = :user_id";

        MapSqlParameterSource likeParams = new MapSqlParameterSource();
        likeParams.addValue("review_id", reviewId);
        likeParams.addValue("user_id", userId);

        if (jdbc.queryForObject(sqlQuery, likeParams, Integer.class) != 0) {
            ret = true;
        }
        return ret;
    }
    @Override
    public void deleteLike(Review review, User user) {
        String sqlQuery = "DELETE FROM reviews_users WHERE review_id = :review_id AND USER_ID = :user_id;";
        MapSqlParameterSource likeParams = new MapSqlParameterSource();
        likeParams.addValue("review_id", review.getReviewId());
        likeParams.addValue("user_id", user.getId());
        jdbc.update(sqlQuery, likeParams);
    }

    @Override
    public Collection<Review> getCountReview(long filmId,int count) {
        String sqlQuery =  "SELECT r.REVIEW_ID , r.user_id, r.film_id, r.content, r.isPositive, " +
                           "(SELECT SUM(like_Dislike) FROM REVIEWS_USERS AS RU WHERE RU.review_id = r.review_id) count_likes " +
                           "FROM reviews AS r WHERE r.film_Id = :film_id " +
                           "ORDER BY count_likes DESC " +
                           "LIMIT :count";

        MapSqlParameterSource reviewParams = new MapSqlParameterSource();
        reviewParams.addValue("film_id", filmId);
        reviewParams.addValue("count", count);

        Collection<Review> reviews = jdbc.query(sqlQuery,reviewParams, rowMapper);

        return reviews;
    }
}
