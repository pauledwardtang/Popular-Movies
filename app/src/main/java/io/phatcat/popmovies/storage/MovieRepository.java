package io.phatcat.popmovies.storage;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.LiveData;
import io.phatcat.popmovies.model.FavoriteMovie;
import io.phatcat.popmovies.model.Movie;

public class MovieRepository {
    private MovieDao mMovieDao;
    private LiveData<List<Movie>> mAllMovies;
    private LiveData<List<Movie>> mFavoriteMovies;

    public MovieRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mMovieDao = db.movieDao();
        mAllMovies = mMovieDao.getMovies();
        mFavoriteMovies = mMovieDao.getFavoriteMovies();
    }

    /**
     * Gets all queried movies
     * @return
     */
    public LiveData<List<Movie>> getMovies() { return mAllMovies; }

    /**
     * Gets favorited movies
     * @return
     */
    public LiveData<List<Movie>> getFavoriteMovies() { return mFavoriteMovies; }

    /**
     * Adds a single movie to the database
     * @param movie
     */
    void insert(Movie movie) {
        new DaoTask<>(mMovieDao, MovieDao::insert).execute(movie);
    }

    /**
     * Inserts all movies (replacing existing movies)
     * @param movies
     */
    void insertAll(List<Movie> movies) {
        new DaoTask<>(mMovieDao, MovieDao::insertAll).execute(movies);
    }

    /**
     * Deletes a movie from the database
     * @param movie
     */
    void delete(Movie movie) {
        new DaoTask<>(mMovieDao, MovieDao::delete).execute(movie);
    }

    /**
     * Removes
     * @param movie
     */
    void setFavorite(Movie movie, boolean favorite) {
        DaoTask<FavoriteMovie> task;
        if (favorite) {
            task = new DaoTask<>(mMovieDao, MovieDao::insertFavorite);
        }
        else {
            task = new DaoTask<>(mMovieDao, MovieDao::deleteFavorite);
        }
        task.execute(new FavoriteMovie(movie.getId()));
    }

    /**
     * Interface for simplified async DAO interactions.
     * @param <Subject>
     */
    private interface DaoInteractor<Subject> {
        void interact(MovieDao dao, Subject subject);
    }

    /**
     * Async task for interacting with the moviedao.
     * @param <Params>
     */
    private static class DaoTask<Params> extends AsyncTask<Params, Void, Void> {
        private MovieDao mDao;
        private DaoInteractor<Params> mInteractor;

        /**
         * @param dao The MovieDAO to interact with
         * @param interactor Simple interface for allowing inline lambdas
         */
        DaoTask(MovieDao dao, DaoInteractor<Params> interactor) {
            mDao = dao;
            mInteractor = interactor;
        }

        @SafeVarargs
        @Override
        protected final Void doInBackground(final Params... params) {
            mInteractor.interact(mDao, params[0]);
            return null;
        }
    }

}
