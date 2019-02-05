package io.phatcat.popmovies.storage;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import io.phatcat.popmovies.model.FavoriteMovie;
import io.phatcat.popmovies.model.Movie;

@Dao
public interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Movie movie);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Movie> movies);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(Movie movie);

    @Query("SELECT * FROM movie")
    LiveData<List<Movie>> getMovies();

    @Query("SELECT * FROM movie where id IN (SELECT movieId from favorite_movies)")
    LiveData<List<Movie>> getFavoriteMovies();

    @Query("SELECT * FROM movie WHERE id=:id")
    LiveData<Movie> getMovie(int id);

    @Delete
    void delete(Movie movie);

    @Query("DELETE FROM movie")
    void deleteAll();

    @Insert
    void insertFavorite(FavoriteMovie favoriteMovie);

    @Delete
    void deleteFavorite(FavoriteMovie favoriteMovie);
}
