package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.DbFilmStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DbFilmStorageImpl implements DbFilmStorage {

    private final JdbcTemplate jdbcTemplate;

        public DbFilmStorageImpl(JdbcTemplate jdbcTemplate) {
            this.jdbcTemplate = jdbcTemplate;
        }
        @Override
        public void addLike(Integer filmId, Integer userId) {
            jdbcTemplate.update("INSERT INTO film_likes(film_id, user_id) " +
                    "VALUES(?,?)", filmId, userId);
        }

        @Override
        public void removeLike(Film film, Integer userId) {
            Integer filmId = film.getId();
            jdbcTemplate.update("DELETE FROM film_likes WHERE user_id = ? AND film_id = ?", userId, filmId);
        }
        @Override
        public List<Film> getTopTenFilms(Integer count) {
            String sql = "SELECT f.*\n" +
                    "FROM films AS f\n" +
                    "JOIN film_likes AS fl ON f.film_id = fl.film_id\n" +
                    "GROUP BY f.film_id\n" +
                    "ORDER BY COUNT(fl.user_id) DESC\n" +
                    "LIMIT ?";
            if(jdbcTemplate.query(sql,this::filmBuilder, count).isEmpty()) {
                return findAllFilms();
            } else {
                return jdbcTemplate.query(sql,this::filmBuilder, count);
            }
        }

        @Override
        public Film addFilm(Film film) {
            if (film.getId() == null) {
                SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                        .withTableName("films")
                        .usingGeneratedKeyColumns("film_id");
                film.setId(simpleJdbcInsert.executeAndReturnKey(filmToMap(film)).intValue());
            } else {
                String filmInsertSql = "INSERT INTO films (film_id, name, description, release_date, duration, mpa_id) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
                jdbcTemplate.update(filmInsertSql,
                        film.getId(),
                        film.getName(),
                        film.getDescription(),
                        film.getReleaseDate(),
                        film.getDuration(),
                        film.getMpa().getId());
            }
            addFilmGenres(film);
            addFilmLikes(film);
            int mpaId = film.getMpa().getId();
            film.setMpa(getMpaById(mpaId));

//            List<Genre> genres = film.getGenres();
//            for(Genre genre: genres) {
//                Genre acquiredGenre = getGenreById(genre.getId());
//                film.getGenres().add(acquiredGenre);
//            }
            updateFilmGenreTable(film);
            return film;
        }
            public static Map<String, Object> filmToMap(Film film) {
                Map<String, Object> values = new HashMap<>();
                values.put("name", film.getName());
                values.put("description", film.getDescription());
                values.put("release_date", film.getReleaseDate());
                values.put("duration", film.getDuration());
                values.put("mpa", film.getMpa());
                return values;
            }

        @Override
        public void removeFilm(Integer id) {
            jdbcTemplate.update("DELETE FROM films WHERE id = ? ", id);
        }

    private Integer insertOrRetrieveGenreId(String genreName) {
        String checkSql = "SELECT id FROM genre WHERE name = ?";
        Integer genreId = jdbcTemplate.queryForObject(checkSql, Integer.class, genreName);

        if (genreId == null) {
            String insertSql = "INSERT INTO genre (name) VALUES (?) RETURNING id";
            genreId = jdbcTemplate.queryForObject(insertSql, Integer.class, genreName);
        }

        return genreId;
    }

    private Integer insertOrRetrieveMpaId(String mpaName) {
        String checkSql = "SELECT id FROM mpa WHERE name = ?";
        Integer mpaId = jdbcTemplate.queryForObject(checkSql, Integer.class, mpaName);

        if (mpaId == null) {
            String insertSql = "INSERT INTO mpa (name) VALUES (?) RETURNING id";
            mpaId = jdbcTemplate.queryForObject(insertSql, Integer.class, mpaName);
        }

        return mpaId;
    }
        @Override
        public Film updateFilm(Film film) {
            String sqlQuery = "UPDATE films SET " +
                "name = ?, description = ?, release_date = ?, duration = ?,  mpa_id = ? " +
                "WHERE film_id = ?";
            int rowsUpdated = jdbcTemplate.update(sqlQuery,
                    film.getName(),
                    film.getDescription(),
                    Date.valueOf(film.getReleaseDate()),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId());
            if (rowsUpdated == 1) {
                log.info("Успешно обновлено", film);
                updateFilmGenreTable(film);
                return film;
            } else {
                int filmId = film.getId();
                throw new NotFoundException("Фильм с id " + filmId + " не найден.");
            }
        }
@Override
public List<Film> findAllFilms() {
    return jdbcTemplate.query(
            "SELECT f.*, m.mpa_name AS mpa_name FROM films AS f INNER JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                    "JOIN film_genre as fg ON f.film_id = fg.film_id JOIN genre AS g ON fg.genre_id = g.genre_id",
            this::filmBuilder);
}


    public Film filmBuilder(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(resultSet.getInt("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(new Mpa(resultSet.getInt("mpa_id"), resultSet.getString("mpa_name")))
                .build();
        addFilmGenres(film);
        addFilmLikes(film);
        Set<Genre> genresTreeSet = new TreeSet<>();
        genresTreeSet.add(buildGenre(resultSet,rowNum));
        film.setGenres(genresTreeSet);
        //film.setMpa(buildMpa(resultSet,rowNum));
        return film;
    }
@Override
public Film getFilmById(Integer id) {
    try {
        String sqlQuery = "SELECT f.*, m.mpa_name FROM films AS f " +
                "INNER JOIN mpa AS m ON f.mpa_id = m.mpa_id AND f.film_id = ? " +
                "JOIN film_genre AS fg ON f.film_id = fg.film_id";
        return jdbcTemplate.queryForObject(sqlQuery, this::filmBuilder, id);
    } catch (EmptyResultDataAccessException e) {
        throw new NotFoundException("Фильм с id=" + id + " не найден.");
    }
}
    @Override
    public Mpa getMpaById(int mpaId) {
        try {
            String sqlQuery = "SELECT * FROM mpa WHERE mpa_id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::buildMpa, mpaId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Mpa с id=" + mpaId + " не найден.");
        }
    }
        private Genre getGenreByFilmId(Integer id) {
            Integer genreId = jdbcTemplate.queryForObject("select fg.genre_id \n" +
                    "from film_genre fg\n" +
                    "where fg.film_id = ?", Integer.class, id);
            String genreName = jdbcTemplate.queryForObject("select g.name \n" +
                    "from film_genre fg join genre g on fg.genre_id = g.id \n" +
                    "where fg.film_id = ?", String.class, id);
            return new Genre(genreId, genreName);
        }
    private RowMapper<Film> filmRowMapper() {
        return (rs, rowNum) -> {
            Film film = new Film();

            Integer id = rs.getInt("film_id");
            String name = rs.getString("name");
            String description = rs.getString("description");
            LocalDateTime releaseDate = rs.getTimestamp("release_date").toLocalDateTime();
            Integer duration = rs.getInt("duration");

            Integer mpaId = rs.getInt("mpa");



            TreeSet<Integer> likes = new TreeSet<>();
            //likes.add(rs.getInt("likes"));

            film.setId(id);
            film.setName(name);
            film.setDescription(description);
            film.setReleaseDate(releaseDate.toLocalDate());
            film.setDuration(duration);
            Mpa mpa = getMpaById(film.getId());
            //film.setMpa(mpa);

            film.setMpa(mpa);
            //film.setLikes(likes);


            return film;
        };
    }
    @Override
    public Genre getGenreById(int id) {
        try {
            String sqlQuery = "SELECT * FROM genre WHERE genre_id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::buildGenre, id);
        } catch (NotFoundException e) {
            throw new NotFoundException("Жанр с id " + id + " не найден");
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        return jdbcTemplate.query("SELECT * FROM genre",
                this::buildGenre);
    }

    public Genre buildGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt("genre_id"), rs.getString("name"));
    }

    @Override
    public List<Mpa> getAllMpa() {
        return jdbcTemplate.query("SELECT * FROM mpa",
                this::buildMpa);
    }

    public Mpa buildMpa(ResultSet rs, int rowNum) throws SQLException {
        return new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name"));
    }
    @Override
    public void removeAllFilms() {
        jdbcTemplate.update("DELETE FROM films");
    }

    @Override
    public Film addFilmGenres(Film film) {
        return null;
    }
//    @Override
//    public Film addFilmGenres(Film film) {
//        if (film.getGenres() != null) {
//            List<Genre> genres = film.getGenres()
//                    .stream()
//                    .distinct()
//                    .collect(Collectors.toList());
//            film.setGenres(genres);
//            List<Integer> genreIds = genres.stream()
//                    .map(Genre::getId)
//                    .collect(Collectors.toList());
//            String sqlQuery = "INSERT INTO film_genre (film_id, genre_id) VALUES (?,?)";
//            jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
//                public void setValues(PreparedStatement preparedStatement, int value)
//                        throws SQLException {
//                    preparedStatement.setInt(1, film.getId());
//                    preparedStatement.setInt(2, genreIds.get(value));
//                }
//
//                public int getBatchSize() {
//                    return genres.size();
//                }
//            });
//        } else {
//            film.setGenres(new ArrayList<Genre>());
//        }
//        return film;
//    }

    @Override
    public void removeFilmGenres(int filmId) {
        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", filmId);
    }

    public List<Integer> getUserLikes(int filmId) {
        return jdbcTemplate.query(
                "SELECT user_id FROM film_likes WHERE film_id = ?", (resultSet, rowNum) ->
                        resultSet.getInt("user_id"), filmId);
    }

    @Override
    public Film addFilmLikes(Film film) {
        String sqlQuery = "SELECT user_id " +
                "FROM film_likes " +
                "WHERE film_id = ?";

        List<Integer> likeUserIds = jdbcTemplate.queryForList(sqlQuery, Integer.class, film.getId());

        TreeSet<Integer> likes = new TreeSet<>(likeUserIds != null ? likeUserIds : new TreeSet<>());

        film.setLikes(likes);

        return film;
    }

    Set<Genre> getFilmGenres(Integer filmId) {

        SqlRowSet genreRows = jdbcTemplate
                .queryForRowSet("SELECT genre_id FROM film_genres WHERE film_id = ?", filmId);

        Set<Genre> genres = new TreeSet<>(Comparator.comparing(Genre::getId));

        while (genreRows.next()) {
            // получение всей информации о жанрах фильма
            Genre filmGenre =
                    getGenreById(genreRows.getInt("genre_id"));
            genres.add(filmGenre);

        }
        return genres;
    }

        private void updateFilmGenreTable(Film film) {
            // получаем жанры фильма
            Set<Genre> genres = film.getGenres();

            // обновляем, либо удаляем данные в таблице
            if (genres != null && !genres.isEmpty()) {

                // получаем информацию о новых id жанров фильма, сравниваем с текущими данными
                Set<Integer> newGenresId = genres.stream().map(Genre::getId).collect(Collectors.toSet());
                Set<Integer> oldGenresId = new HashSet<>();

                SqlRowSet oldGenres = jdbcTemplate
                        .queryForRowSet("SELECT * FROM film_genre WHERE film_id = ?", film.getId());

                while (oldGenres.next()) {
                    oldGenresId.add(oldGenres.getInt("genre_id"));
                }

                // выбираем данные для удаления
                oldGenresId.removeAll(newGenresId);

                // удаляем необходимые данные
                String sqlQueryDel = "delete from film_genre where genre_id = ? and film_id = ?";
                oldGenresId.forEach(genreId -> jdbcTemplate.update(sqlQueryDel, genreId, film.getId()));

                // дополняем данные
                String sqlQuerySelect = "SELECT COUNT(*) FROM film_genre WHERE film_id = ? AND genre_id = ?";
                String sqlQueryInsert = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
                String sqlQueryUpdate = "UPDATE film_genre SET film_id = ?, genre_id = ? WHERE film_id = ? AND genre_id = ?";

                genres.stream().map(Genre::getId).forEach(genreId -> {
                    int count = jdbcTemplate.queryForObject(sqlQuerySelect, Integer.class, film.getId(), genreId);
                    if (count > 0) {
                        jdbcTemplate.update(sqlQueryUpdate, film.getId(), genreId, film.getId(), genreId);
                    } else {
                        jdbcTemplate.update(sqlQueryInsert, film.getId(), genreId);
                    }
                });


                log.info("Обновлена информация о жанрах у фильма {}", film.getId());


            } else { // удаление всех данных о жанрах в случае пустого списка

                String sqlQueryDeleteGenres = "DELETE FROM film_genre WHERE film_id = ?";

                jdbcTemplate.update(sqlQueryDeleteGenres, film.getId());

                log.info("Удалена информация о жанрах у фильма {}", film.getId());

            }

        }

}

