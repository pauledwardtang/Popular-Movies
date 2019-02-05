package io.phatcat.popmovies.storage;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import io.phatcat.popmovies.model.Movie;

public class MovieViewModel extends AndroidViewModel {
    private MovieRepository mRepository;
    private LiveData<List<Movie>> mAllMovies;
    private LiveData<List<Movie>> mFavoriteMovies;

    public MovieViewModel(@NonNull Application application) {
        super(application);
        mRepository = new MovieRepository(application);
        mAllMovies = mRepository.getMovies();
        mFavoriteMovies = mRepository.getFavoriteMovies();
    }

    public LiveData<List<Movie>> getMovies() { return mAllMovies; }
    public LiveData<List<Movie>> getFavoriteMovies() { return mFavoriteMovies; }

    public void insert(Movie movie) { mRepository.insert(movie); }
    public void insertAll(List<Movie> movies) { mRepository.insertAll(movies); }
    public void delete(Movie movie) { mRepository.delete(movie); }

    public void setFavorite(Movie movie, boolean favorite) {
        mRepository.setFavorite(movie, favorite);
    }
}
