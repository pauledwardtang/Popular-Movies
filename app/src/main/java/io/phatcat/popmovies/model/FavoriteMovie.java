package io.phatcat.popmovies.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorite_movies")
public class FavoriteMovie {
    @PrimaryKey
    @ForeignKey(entity = Movie.class, parentColumns = "id", childColumns = "movieId")
    public int movieId;

    public FavoriteMovie() {}

    @Ignore
    public FavoriteMovie(int id) {
        movieId = id;
    }
}
