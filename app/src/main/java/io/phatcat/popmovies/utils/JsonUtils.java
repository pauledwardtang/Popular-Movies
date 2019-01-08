package io.phatcat.popmovies.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.phatcat.popmovies.model.Movie;

public final class JsonUtils {
    @Nullable
    public static List<Movie> parseMovies(String json) {
        List<Movie> movies = new ArrayList<>();

        try {
            JSONObject root = new JSONObject(json);
            JSONArray moviesJson = root.getJSONArray("results");
            for (int i = 0; i < moviesJson.length(); i++) {
                Movie movie = parseMovie((JSONObject) moviesJson.get(i));
                if (movie != null) {
                    movies.add(movie);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return movies;
    }

    @Nullable
    public static Movie parseMovie(@NonNull String json) {
        try {
            return parseMovie(new JSONObject(json));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    private static Movie parseMovie(@NonNull JSONObject json) {
        Movie movie = new Movie();

        try {
            movie.setTitle(json.getString("title"));
            movie.setPosterUrl(json.getString("poster_path"));
            movie.setSynopsis(json.getString("overview"));
            movie.setRating(json.getDouble("vote_average"));
            movie.setReleaseDate(json.getString("release_date"));
            return movie;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
