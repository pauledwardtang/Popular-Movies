package io.phatcat.popmovies.network;

import java.util.List;

import io.phatcat.popmovies.model.Movie;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

interface MovieDbService {
    @GET("movie/{sortBy}")
    Call<List<Movie>> getMovies(@Path("sortBy") String sortBy, @Query("api_key") String apiKey);
}
