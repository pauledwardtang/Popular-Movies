package io.phatcat.popmovies.network;

import android.util.Log;

import java.security.InvalidParameterException;
import java.util.List;

import io.phatcat.popmovies.BuildConfig;
import io.phatcat.popmovies.MovieSortType;
import io.phatcat.popmovies.model.Movie;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Assumes only a single caller is accessing this network service
 */
public final class MovieNetworkService {
    private static String TAG = MovieNetworkService.class.getSimpleName();
    private static MovieNetworkService INSTANCE = null;

    private volatile Call<List<Movie>> mCurrentCall;

    private OnPostExecuteListener<List<Movie>> mListener = null;
    private MovieDbService mMovieService;

    /**
     * Disallow instantiation
     */
    private MovieNetworkService() {}

    public static MovieNetworkService getInstance() {
        if (INSTANCE == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.API_BASE_URL)
                    .addConverterFactory(MovieConverterFactory.create())
                    .build();

            INSTANCE = new MovieNetworkService();
            INSTANCE.mMovieService = retrofit.create(MovieDbService.class);
        }
        return INSTANCE;
    }

    /**
     * Callers MUST call {@link #removeListeners()} to avoid a leak
     * @param onPostExecuteListener the listener
     */
    public void setListener(OnPostExecuteListener<List<Movie>> onPostExecuteListener) {
        mListener = onPostExecuteListener;
    }

    public void removeListeners() {
        mListener = null;
    }

    /**
     * Asynchronously fetches movies based on the sort type.
     */
    public void loadMovies(MovieSortType sortType) {
        if (mCurrentCall != null) {
            return;
        }
        synchronized (this) {
            doLoadMovies(sortType);
        }
    }

    private void doLoadMovies(MovieSortType sortType) {
        String sortBy;
        switch (sortType) {
            case POPULAR:
                sortBy = "popular";
                break;
            case TOP_RATED:
                sortBy = "top_rated";
                break;
            default:
                throw new InvalidParameterException("Invalid parameter given: " + sortType.name());
        }

        mCurrentCall = mMovieService.getMovies(sortBy, BuildConfig.API_KEY);
        mCurrentCall.enqueue(mSingleCallback);
    }

    private Callback<List<Movie>> mSingleCallback = new Callback<List<Movie>>() {
        @Override
        public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
            mCurrentCall = null;

            if (mListener != null) {
                mListener.onPostExecute(response.body());
            }
            else {
                Log.w(TAG, "Movies loaded, but no listener to callback");
            }
        }

        @Override
        public void onFailure(Call<List<Movie>> call, Throwable t) {
            mCurrentCall = null;
            t.printStackTrace();
            if (mListener != null) {
                mListener.onPostExecute(null);
            }
            else {
                Log.w(TAG, "Movies failed to load, but no listener to callback");
            }
        }
    };

}
