package io.phatcat.popmovies.network;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.phatcat.popmovies.model.Movie;
import io.phatcat.popmovies.utils.JsonUtils;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Using an existing converter is too easy! Lets "retrofit" our own! This only handles lists of movies
 * and not individual movie calls. This also doesn't allow us to write movie data, which is ok.
 */
final class MovieConverterFactory extends Converter.Factory {
    static MovieConverterFactory create() {
        return new MovieConverterFactory();
    }

    private MovieConverterFactory() {}

    /**
     * Returns a converter which returns a list of movies from an HTTP response.
     */
    @Nullable
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type,
                                                            Annotation[] annotations,
                                                            Retrofit retrofit) {
        return new MovieResponseBodyConverter();
    }

    private static class MovieResponseBodyConverter implements Converter<ResponseBody, List<Movie>> {
        @Nullable
        @Override
        public List<Movie> convert(@NonNull ResponseBody value) {
            List<Movie> moviesList = null;
            try {
                String response = value.string();
                moviesList = JsonUtils.parseMovies(response);
            }
            catch (IOException | OutOfMemoryError e) {
                e.printStackTrace();
            }
            return moviesList;
        }
    }
}
