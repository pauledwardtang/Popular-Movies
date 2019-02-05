package io.phatcat.popmovies.network;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Function;

import androidx.annotation.Nullable;
import io.phatcat.popmovies.model.Movie;
import io.phatcat.popmovies.model.MovieReviewPage;
import io.phatcat.popmovies.model.MovieTrailers;
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
        Class<?> clazz = getRawType(type);
        if (clazz == MovieReviewPage.class) {
            return new ResponseConverter<>(JsonUtils::parseReviews);
        }
        else if (clazz == MovieTrailers.class) {
            return new ResponseConverter<>(JsonUtils::parseTrailers);
        }
        else if (clazz == Movie.class) {
            return new ResponseConverter<>(JsonUtils::parseMovie);
        }
        else if (clazz == List.class) {
            // Here be dragons! :D
            Type parameterizedType = getParameterUpperBound(0, (ParameterizedType) type);
            Class clazzier = getRawType(parameterizedType);
            if (clazzier == Movie.class) {
                return new ResponseConverter<>(JsonUtils::parseMovies);
            }
        }
        return null;
    }

}
