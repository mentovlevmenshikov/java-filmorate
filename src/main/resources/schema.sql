DROP TABLE IF EXISTS films_genres;
DROP TABLE IF EXISTS films_likes;
DROP TABLE IF EXISTS FILMS;
DROP TABLE IF EXISTS mpa;
DROP TABLE IF EXISTS GENRES;
DROP TABLE IF EXISTS FRIENDS;
DROP TABLE IF EXISTS USERS;


CREATE TABLE IF NOT EXISTS users (
	user_id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
	email varchar(50) NOT NULL CONSTRAINT user_email_check CHECK (email LIKE '%_@_%.__%'),
	login varchar(30) NOT NULL,
	name varchar(100) NOT NULL,
	birthday DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS friends (
    user_id   BIGINT CONSTRAINT friends_user_id_fk REFERENCES users(user_id) ON DELETE CASCADE NOT NULL,
    friend_id BIGINT CONSTRAINT friends_friend_id_fk REFERENCES users(user_id) ON DELETE CASCADE NOT NULL,
    status    BIGINT CONSTRAINT user_friends_status_check CHECK (status IN (0, 1)) NOT NULL,
    CONSTRAINT friends_user_id_friend_id_unique UNIQUE (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS genres (
    genre_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    genre_name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS mpa (
    mpa_id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    mpa_name VARCHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
    film_id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(200),
    release_date DATE NOT NULL,
    duration BIGINT NOT NULL CONSTRAINT film_duration_check CHECK (duration > 0),
    mpa_id BIGINT NOT NULL CONSTRAINT films_mpa_id_fk REFERENCES mpa(mpa_id)
);

CREATE TABLE IF NOT EXISTS films_genres (
    film_id BIGINT NOT NULL CONSTRAINT films_genres_film_id_fk REFERENCES films(film_id),
    genre_id BIGINT NOT NULL CONSTRAINT films_genres_genres_id_fk REFERENCES genres(genre_id),
    CONSTRAINT films_genres_film_genres_ids_unique UNIQUE (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS films_likes (
    film_id BIGINT CONSTRAINT films_likes_film_id_fk REFERENCES films(film_id) NOT NULL,
    user_id BIGINT CONSTRAINT films_likes_user_id_fk REFERENCES users(user_id) NOT NULL,
    CONSTRAINT films_likes_film_user_ids_unique UNIQUE (film_id, user_id)
);
