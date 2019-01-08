package io.phatcat.popmovies.network;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import androidx.annotation.NonNull;
import io.phatcat.popmovies.MovieSortType;
import io.phatcat.popmovies.model.Movie;
import io.phatcat.popmovies.utils.JsonUtils;
import io.phatcat.popmovies.utils.NetworkUtils;

import static io.phatcat.popmovies.MovieSortType.POPULAR;

/**
 * AsyncTask for retrieving movies from TheMovieDb service
 */
public class MoviesAsyncTask extends AsyncTask<MovieSortType, Void, List<Movie>> {
    private OnPostExecuteListener<List<Movie>> mListener;

    public MoviesAsyncTask(@NonNull OnPostExecuteListener<List<Movie>> listener) {
        mListener = listener;
    }

    @Override
    protected List<Movie> doInBackground(MovieSortType... sortTypes) {
        if (sortTypes.length == 0 || sortTypes.length > 1) {
            throw new IllegalArgumentException("Must only specify one sort type");
        }

        MovieSortType sortType = sortTypes[0];
        List<Movie> moviesList = null;
        String sortPath = (sortType == POPULAR) ?
                NetworkUtils.PATH_POPULAR_MOVIES : NetworkUtils.PATH_TOP_RATED_MOVIES;

        URL url = NetworkUtils.buildUrl(sortPath);

        if (url != null) {
            try {
                String response = NetworkUtils.getResponseFromHttpUrl(url);
                moviesList = JsonUtils.parseMovies(response);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return moviesList;
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        mListener.onPostExecute(movies);

        // Avoid a memory leak
        mListener = null;
    }
}
