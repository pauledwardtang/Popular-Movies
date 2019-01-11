package io.phatcat.popmovies.network;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import io.phatcat.popmovies.MovieSortType;

@RunWith(AndroidJUnit4.class)
public class MovieNetworkServiceTest {

    /**
     * Simple sanity check, not truly testing a network call here
     */
    @Test
    public void loadMovies() {
        MovieNetworkService.getInstance().loadMovies(MovieSortType.POPULAR);
    }
}
