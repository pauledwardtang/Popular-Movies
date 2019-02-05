package io.phatcat.popmovies.network;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import io.phatcat.popmovies.MovieSortType;
import retrofit2.Call;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class MovieNetworkServiceTest {
    private MovieNetworkService service = MovieNetworkService.getInstance(true);

    @Test
    public void loadMoviesSanityCheck() {
        service.loadMovies(MovieSortType.POPULAR);
    }

    @Test
    public void loadMovieTrailersSanityCheck() {
        Call call = service.loadTrailers(424694, null);
        try {
            assertNotNull(call.execute());
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void loadMovieReviewsSanityCheck() {
        Call call = service.loadReviews(424694, null, null);
        try {
            assertNotNull(call.execute());
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
