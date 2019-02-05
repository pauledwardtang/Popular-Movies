package io.phatcat.popmovies.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.phatcat.popmovies.model.Movie;
import io.phatcat.popmovies.model.MovieTrailers;
import io.phatcat.popmovies.model.MovieReviewPage;

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

        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    private static Movie parseMovie(@NonNull JSONObject json) {
        Movie movie = new Movie();

        try {
            movie.setId(json.getInt("id"));
            movie.setTitle(json.getString("title"));
            movie.setPosterUrl(json.getString("poster_path"));
            movie.setSynopsis(json.getString("overview"));
            movie.setRating(json.getDouble("vote_average"));
            movie.setReleaseDate(json.getString("release_date"));
            return movie;

        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static MovieTrailers parseTrailers(String json) {
        if (json == null) return null;

        try {
            MovieTrailers movieTrailers = new MovieTrailers();
            JSONObject root = new JSONObject(json);

            movieTrailers.id = root.getInt("id");

            JSONArray results = root.getJSONArray("results");
            movieTrailers.trailerList = new ArrayList<>(results.length());
            JSONObject trailerInfoObject;
            for (int i = 0; i < results.length(); i++) {
                trailerInfoObject = results.getJSONObject(i);

                MovieTrailers.TrailerInfo trailerInfo = new MovieTrailers.TrailerInfo();
                trailerInfo.name = trailerInfoObject.getString("name");
                trailerInfo.key = trailerInfoObject.getString("key");
                trailerInfo.site = trailerInfoObject.getString("site");

                // Official API shows this is an int, but in practice it could be a String eg HD
                trailerInfo.size = getIntOrString(trailerInfoObject, "size")+ "p";
                trailerInfo.type = trailerInfoObject.getString("type");

                movieTrailers.trailerList.add(trailerInfo);
            }
            return movieTrailers;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static MovieReviewPage parseReviews(String json) {
        if (json == null) return null;
        try {
            MovieReviewPage movieReviewPage = new MovieReviewPage();

            JSONObject root = new JSONObject(json);
            movieReviewPage.id = root.getInt("id");
            movieReviewPage.page = root.getInt("page");
            movieReviewPage.totalPages = root.getInt("total_pages");
            movieReviewPage.totalResults = root.getInt("total_results");

            JSONArray results = root.getJSONArray("results");
            movieReviewPage.reviewList = new ArrayList<>(results.length());
            JSONObject result;
            for (int i = 0; i < results.length(); i++) {
                result = results.getJSONObject(i);

                MovieReviewPage.ReviewInfo review = new MovieReviewPage.ReviewInfo();
                review.author = result.getString("author");
                review.content = result.getString("content");
                review.id = result.getString("id");
                review.url = result.getString("url");

                movieReviewPage.reviewList.add(review);
            }

            return movieReviewPage;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    private static String getIntOrString(JSONObject object, String key) {
        try {
            int value = object.getInt(key);
            return String.valueOf(value);
        }
        catch (Exception e) {
            try { return object.getString(key); }
            catch (Exception e1) {
                e.printStackTrace();
                e1.printStackTrace();
                return null;
            }
        }
    }
}
