package io.phatcat.popmovies.network;

import java.util.List;

import io.phatcat.popmovies.model.Movie;
import io.phatcat.popmovies.model.MovieTrailers;
import io.phatcat.popmovies.model.MovieReviewPage;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

interface MovieDbService {
    @GET("movie/{sortBy}")
    Call<List<Movie>> getMovies(@Path("sortBy") String sortBy, @Query("api_key") String apiKey);

    @GET("movie/{id}/videos")
    Call<MovieTrailers> getTrailers(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("movie/{id}/reviews")
    Call<MovieReviewPage> getReviews(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("movie/{id}/reviews")
    Call<MovieReviewPage> getReviewsByPage(@Path("id") int id, @Query("page") int page,
                                           @Query("api_key") String apiKey);

}
