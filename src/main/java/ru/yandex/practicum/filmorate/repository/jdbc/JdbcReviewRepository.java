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
import ru.yandex.practicum.filmorate.repository.ReviewLikeStorage;

import java.util.*;

@Repository
@Slf4j
public class JdbcReviewRepository extends JdbcBaseRepository<Review> implements ReviewLikeStorage {
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
                " WHERE r.review_id = :r_id";
        MapSqlParameterSource reviewParameterSource = new MapSqlParameterSource();
        reviewParameterSource.addValue("r_id", id);
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
                "VALUES (:content, :isPositive, :userId, :filmId);";

        MapSqlParameterSource reviewParams = new MapSqlParameterSource();
        reviewParams.addValue("filmId", review.getFilmId());
        reviewParams.addValue("userId", review.getUserId());
        reviewParams.addValue("content", review.getContent());
        reviewParams.addValue("isPositive", review.getIsPositive());
        jdbc.update(sqlInsert, reviewParams, keyHolder);
        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return review;
    }

    @Override
    public Review update(Review review) {
        String sqlUpdate = " UPDATE REVIEWS SET CONTENT = :content, ISPOSITIVE = :isPositive, " +
                " USER_ID = :userId, FILM_ID = :filmId" +
                " WHERE REVIEW_ID = :reviewId;";

        MapSqlParameterSource reviewParams = new MapSqlParameterSource();
        reviewParams.addValue("reviewId", review.getReviewId());
        reviewParams.addValue("filmId", review.getFilmId());
        reviewParams.addValue("userId", review.getUserId());
        reviewParams.addValue("content", review.getContent());
        reviewParams.addValue("isPositive", review.getIsPositive());

        jdbc.update(sqlUpdate, reviewParams);

        return review;
    }

    public void delete(Review review) {
        String sqlDelete = "DELETE FROM REVIEWS WHERE REVIEW_ID = :reviewid; ";

        MapSqlParameterSource reviewParams = new MapSqlParameterSource();
        reviewParams.addValue("r_id", review.getReviewId());

        jdbc.update(sqlDelete, reviewParams);
        this.deleteReviewUsers(review.getReviewId());
    }

    private void deleteReviewUsers(Long  id) {
        String sqlDelete = "DELETE FROM reviews_users WHERE REVIEW_ID = :reviewid; ";

        MapSqlParameterSource reviewParams = new MapSqlParameterSource();
        reviewParams.addValue("r_id", id);
        jdbc.update(sqlDelete, reviewParams);
    }

    @Override
    public void addLike(Review review, User user) {
        String sqlQuery = "INSERT INTO reviews_users (review_id, user_id, like_Dislike) VALUES (:review_id, :user_id, :like)";
        jdbc.update(sqlQuery, Map.of("review_id", review.getReviewId(), "user_id", user.getId(),"like",1));
        //return getById(review.getReviewId()).get();
    }
    @Override
    public void addDisLike(Review review, User user) {
        String sqlQuery = "INSERT INTO reviews_users (review_id, user_id, like_Dislike) VALUES (:review_id, :user_id, :like)";
        jdbc.update(sqlQuery, Map.of("review_id", review.getReviewId(), "user_id", user.getId(),"dislike",-1));
        //return getById(review.getReviewId()).get();
    }

    @Override
    public void deleteLike(Review review, User user) {
        String sqlQuery = "DELETE FROM reviews_users WHERE review_id = :review_id AND USER_ID = :user_id;";
        jdbc.update(sqlQuery, Map.of("review_id", review.getReviewId(), "user_id", user.getId()));
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
