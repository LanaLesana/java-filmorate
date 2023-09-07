package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
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
import java.time.ZoneId;
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
        String sql = "SELECT *\n" +
                "FROM films AS f\n" +
                "JOIN film_likes AS fl ON f.film_id = fl.film_id\n" +
                "JOIN MPA m ON f.MPA_ID = m.MPA_ID \n" +
                "GROUP BY f.film_id\n" +
                "ORDER BY COUNT(fl.user_id) DESC\n" +
                "LIMIT ?";
        if (jdbcTemplate.query(sql, this::filmBuilder, count).isEmpty()) {
            return findAllFilms();
        } else {
            return jdbcTemplate.query(sql, this::filmBuilder, count);
        }
    }

    @Override
    public Film addFilm(Film film) {
        java.util.Date date = Date.from(film.getReleaseDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement("INSERT INTO films (name, description, release_date, duration, mpa_id) " +
                    "VALUES (?, ?, ?, ?, ?)", new String[]{"film_id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, sqlDate);
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        Integer keyFilm = keyHolder.getKey().intValue();

        if (film.getGenres() == null) {
            return getFilmById(keyFilm);
        }
        List<Genre> genresList = new ArrayList<>();
        for (Genre genres : film.getGenres()) {
            genresList.add(genres);
        }

        jdbcTemplate.batchUpdate("INSERT INTO FILM_GENRE (film_id, genre_id) VALUES(?,?);", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, keyFilm);
                ps.setInt(2, genresList.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return genresList.size();
            }
        });
        return getFilmById(keyFilm);
    }

    public static Map<String, Object> filmToMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("mpa", film.getMpa());
        values.put("genres", film.getGenres());
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
        String sql = "UPDATE films SET " +
                "name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?" +
                " WHERE film_id = ?";
        if (jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId()) > 0) {
            deleteAllGenresFromFilm(film.getId());
            film.getGenres().forEach(genre -> addGenreToFilm(film.getId(), genre.getId()));

            Film filmToReturn = getFilmById(film.getId());

            return filmToReturn;
        }
        log.warn("Фильм с id {} не найден. ", film.getId());
        throw new NotFoundException("Фильм с id " + film.getId() + " не найден.");
    }

    @Override
    public boolean deleteAllGenresFromFilm(long filmId) {
        String sql = "DELETE FROM film_genre WHERE film_id = ?";
        return jdbcTemplate.update(sql, filmId) > 0;
    }

    @Override
    public boolean addGenreToFilm(long filmId, int genreId) {
        String sql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        return jdbcTemplate.update(sql, filmId, genreId) > 0;
    }

    @Override
    public List<Film> findAllFilms() {
        return jdbcTemplate.query(
                "SELECT f.*, m.mpa_name AS mpa_name FROM films AS f LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                        "LEFT JOIN film_genre as fg ON f.film_id = fg.film_id LEFT JOIN genre AS g ON fg.genre_id = g.genre_id",
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
        addFilmLikes(film);
        Set<Genre> genresHashSet = new HashSet<>();
        for (Genre genre : getGenreByFilmId(film.getId())) {
            genresHashSet.add(genre);
        }

        if (genresHashSet.isEmpty()) {
            film.setGenres(genresHashSet);
            return film;
        }

        film.setGenres(genresHashSet);
        System.out.println(film);
        return film;
    }

    @Override
    public Film getFilmById(Integer id) {
        try {
            String sqlQuery = "SELECT DISTINCT f.*, m.mpa_name FROM films AS f " +
                    "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                    "LEFT JOIN film_genre AS fg ON f.film_id = fg.film_id WHERE f.film_id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::filmBuilder, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильм с id " + id + " не найден.");
        }
    }

    @Override
    public Genre getGenreById(int id) {
        try {
            String sqlQuery = "SELECT * FROM genre WHERE genre_id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::buildGenre, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Mpa с id=" + id + " не найден.");
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

            film.setId(id);
            film.setName(name);
            film.setDescription(description);
            film.setReleaseDate(releaseDate.toLocalDate());
            film.setDuration(duration);
            Mpa mpa = getMpaById(film.getId());

            film.setMpa(mpa);


            return film;
        };
    }

    @Override
    public List<Genre> getGenreByFilmId(int id) {
        String sqlQuery = "SELECT g.GENRE_ID, g.NAME FROM film_genre fg " +
                "LEFT JOIN genre g ON fg.GENRE_ID = g.GENRE_ID WHERE fg.FILM_ID = ? ORDER BY genre_id";
        return jdbcTemplate.query(sqlQuery, new RowMapper<Genre>() {
            @Override
            public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
                Genre genre = new Genre();
                genre.setId(rs.getInt("GENRE_ID"));
                genre.setName(rs.getString("name"));
                return genre;
            }
        }, id);
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
                .queryForRowSet("SELECT genre_id FROM film_genre WHERE film_id = ?", filmId);

        Set<Genre> genres = new TreeSet<>(Comparator.comparing(Genre::getId));

        while (genreRows.next()) {
            Genre filmGenre =
                    getGenreById(genreRows.getInt("genre_id"));
            genres.add(filmGenre);

        }
        return genres;
    }

    private void updateFilmGenreTable(Film film) {
        Set<Genre> genres = film.getGenres();
        if (genres != null && !genres.isEmpty()) {
            Set<Integer> newGenresId = genres.stream().map(Genre::getId).collect(Collectors.toSet());
            Set<Integer> oldGenresId = new HashSet<>();

            SqlRowSet oldGenres = jdbcTemplate
                    .queryForRowSet("SELECT * FROM film_genre WHERE film_id = ?", film.getId());

            while (oldGenres.next()) {
                oldGenresId.add(oldGenres.getInt("genre_id"));
            }
            oldGenresId.removeAll(newGenresId);
            String sqlQueryDel = "delete from film_genre where genre_id = ? and film_id = ?";
            oldGenresId.forEach(genreId -> jdbcTemplate.update(sqlQueryDel, genreId, film.getId()));

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


        } else {

            String sqlQueryDeleteGenres = "DELETE FROM film_genre WHERE film_id = ?";

            jdbcTemplate.update(sqlQueryDeleteGenres, film.getId());

            log.info("Удалена информация о жанрах у фильма {}", film.getId());

        }

    }

}

