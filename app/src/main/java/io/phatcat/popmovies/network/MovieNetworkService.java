package io.phatcat.popmovies.network;

import android.util.Log;

import java.security.InvalidParameterException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import io.phatcat.popmovies.BuildConfig;
import io.phatcat.popmovies.MovieSortType;
import io.phatcat.popmovies.model.Movie;
import io.phatcat.popmovies.model.MovieReviewPage;
import io.phatcat.popmovies.model.MovieTrailers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static okhttp3.logging.HttpLoggingInterceptor.Level;

/**
 * Assumes only a single caller is accessing this network service
 */
public final class MovieNetworkService {
    private static String TAG = MovieNetworkService.class.getSimpleName();
    private static MovieNetworkService INSTANCE = null;
    private static final boolean ENABLE_VERBOSE_LOGGING = false;

    private volatile Call<List<Movie>> mCurrentCall;

    private OnPostExecuteListener<List<Movie>> mListener = null;
    private MovieDbService mMovieService;
    private boolean mAsyncExecution = true;

    /**
     * Disallow instantiation
     */
    private MovieNetworkService() {}

    public static MovieNetworkService getInstance() {
        if (INSTANCE == null) {
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

            // Allow logging for debug builds
            if (BuildConfig.DEBUG) {
                Level logLevel = (ENABLE_VERBOSE_LOGGING) ? Level.BODY : Level.BASIC;

                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(logLevel);
                clientBuilder.addInterceptor(logging);
            }

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.API_BASE_URL)
                    .client(clientBuilder.build())
                    .addConverterFactory(MovieConverterFactory.create())
                    .build();

            INSTANCE = new MovieNetworkService();
            INSTANCE.mMovieService = retrofit.create(MovieDbService.class);
        }
        return INSTANCE;
    }

    @VisibleForTesting
    static MovieNetworkService getInstance(boolean forceSynchronous) {
        MovieNetworkService service = getInstance();
        service.mAsyncExecution = !forceSynchronous;
        return service;
    }

    /**
     * Callers MUST call {@link #removeListeners()} to avoid a leak
     * @param onPostExecuteListener the listener
     */
    public void addListener(OnPostExecuteListener<List<Movie>> onPostExecuteListener) {
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
            onLoadMovies(sortType);
        }
    }

    private void onLoadMovies(MovieSortType sortType) {
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
        public void onResponse(@NonNull Call<List<Movie>> call,
                               @NonNull Response<List<Movie>> response) {
            mCurrentCall = null;

            if (mListener != null) {
                mListener.onPostExecute(response.body());
            }
            else {
                Log.w(TAG, "Movies loaded, but no listener to callback");
            }
        }

        @Override
        public void onFailure(@NonNull Call<List<Movie>> call, Throwable t) {
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

    /**
     * Asynchronously fetches trailer data for a given Movie id. MUST be canceled on orientation
     * change.
     * @param movieId The movie ID for which to fetch the trailer data
     * @param listener Listener to receive the data, calls back with null if there was an error
     */
    public Call<MovieTrailers> loadTrailers(int movieId,
                                            final OnPostExecuteListener<MovieTrailers> listener) {
        synchronized (this) {
            Call<MovieTrailers> call = mMovieService.getTrailers(movieId, BuildConfig.API_KEY);
            if (mAsyncExecution) {
                call.enqueue(new Callback<MovieTrailers>() {
                    @Override
                    public void onResponse(@NonNull Call<MovieTrailers> call,
                                           @NonNull Response<MovieTrailers> response) {
                        if (listener != null)
                            listener.onPostExecute(response.body());
                    }

                    @Override
                    public void onFailure(@NonNull Call<MovieTrailers> call, @NonNull Throwable t) {
                        Log.e(TAG, "Failure loading videos", t);
                        if (listener != null)
                            listener.onPostExecute(null);
                    }
                });
            }
            return call;
        }
    }

    /**
     * Asynchronously fetches reviews for a given Movie id. MUST be canceled on orientation change.
     * @param movieId The movie ID for which to fetch the reviews
     * @param listener Listener to receive the data, calls back with null if there was an error
     * @return
     */
    public Call<MovieReviewPage> loadReviews(int movieId,
        @NonNull final OnPostExecuteListener<MovieReviewPage> listener) {
        return loadReviews(movieId, null, listener);
    }

    /**
     * Asynchronously fetches reviews for a given Movie id. MUST be canceled on orientation change.
     * @param movieId The movie ID for which to fetch the reviews
     * @param pageNumber Optional pageNumber for paging results
     * @param listener Listener to receive the data, calls back with null if there was an error
     * @return
     */
    @VisibleForTesting()
    public Call<MovieReviewPage> loadReviews(int movieId, @Nullable Integer pageNumber,
                                             final OnPostExecuteListener<MovieReviewPage> listener) {
        synchronized (this) {
            Call<MovieReviewPage> call;
            if (pageNumber == null) {
                call = mMovieService.getReviews(movieId, BuildConfig.API_KEY);
            }
            else {
                call = mMovieService.getReviewsByPage(movieId, pageNumber, BuildConfig.API_KEY);
            }

            if (mAsyncExecution) {
                call.enqueue(new Callback<MovieReviewPage>() {
                    @Override
                    public void onResponse(@NonNull Call<MovieReviewPage> call,
                                           @NonNull Response<MovieReviewPage> response) {
                        if (listener != null)
                            listener.onPostExecute(response.body());
                    }

                    @Override
                    public void onFailure(@NonNull Call<MovieReviewPage> call, @NonNull Throwable t) {
                        Log.e(TAG, "Failure loading reviews", t);
                        if (listener != null)
                            listener.onPostExecute(null);
                    }
                });
            }
            return call;
        }
    }

}
