package io.phatcat.popmovies.moviedetails;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Group;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.phatcat.popmovies.R;
import io.phatcat.popmovies.model.Movie;
import io.phatcat.popmovies.model.MovieReviewPage;
import io.phatcat.popmovies.model.MovieTrailers;
import io.phatcat.popmovies.network.MovieNetworkService;
import io.phatcat.popmovies.storage.MovieViewModel;
import io.phatcat.popmovies.utils.ImageUtils;
import retrofit2.Call;

// Ignore ButterKnife access warnings, fields cannot be private.
@SuppressWarnings("WeakerAccess")
public class MovieDetailsActivity extends AppCompatActivity {

    private static final String TAG = MovieDetailsActivity.class.getSimpleName();
    public static final String ARG_MOVIE = "ARG_MOVIE";
    public static final String ARG_TRAILERS = "ARG_TRAILERS";
    public static final String ARG_REVIEWS = "ARG_REVIEWS";

    @BindView(R.id.toolbarLayout) CollapsingToolbarLayout mToolbarLayout;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.title) TextView mTitle;

    @BindView(R.id.progressBar) ProgressBar mProgressBar;
    @BindView(R.id.posterImage) ImageView mPosterImage;
    @BindView(R.id.contentGroup) Group mContentGroup;
    @BindView(R.id.synopsisTv) TextView mSynopsisTv;
    @BindView(R.id.userRatingTv) TextView mUserRatingTv;
    @BindView(R.id.releaseDateTv) TextView mReleaseDateTv;

    @BindView(R.id.trailersLabel) TextView mTrailersLabel;
    @BindView(R.id.reviewsLabel) TextView mReviewsLabel;
    @BindView(R.id.trailerList) RecyclerView mTrailersRecyclerView;
    @BindView(R.id.reviewsList) RecyclerView mReviewsRecyclerView;

    private MovieTrailerRecyclerViewAdapter mTrailersAdapter;
    private MovieReviewRecyclerViewAdapter mReviewsAdapter;

    private Movie mMovie;
    private MovieTrailers mMovieTrailers;
    private MovieReviewPage mMovieReviewPage;

    private Call<MovieTrailers> mTrailersCall;
    private Call<MovieReviewPage> mReviewsCall;

    private MovieViewModel mMovieViewModel;

    private volatile boolean mIsFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        // Setup actionbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        Bundle bundle = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
        if (bundle == null) {
            throw new IllegalStateException();
        }

        mMovie = bundle.getParcelable(ARG_MOVIE);

        if (mMovie == null) {
            Log.e(TAG, "No movie specified");
            Toast.makeText(this, R.string.error_no_movie_detail, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        mTitle.setText(mMovie.getTitle());
        mSynopsisTv.setText(mMovie.getSynopsis());
        mUserRatingTv.setText(String.valueOf(mMovie.getRating()));
        mReleaseDateTv.setText(mMovie.getReleaseDate());

        mTrailersAdapter = new MovieTrailerRecyclerViewAdapter(Collections.emptyList());
        mTrailersRecyclerView.setAdapter(mTrailersAdapter);
        mTrailersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mReviewsAdapter = new MovieReviewRecyclerViewAdapter(Collections.emptyList());
        mReviewsRecyclerView.setAdapter(mReviewsAdapter);
        mReviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mMovieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        mMovieViewModel.getFavoriteMovies().observe(this, movies -> invalidateOptionsMenu());

        // Load poster
        mContentGroup.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);

        loadPoster();
        loadTrailers(bundle);
        loadReviews(bundle);
    }

    @Override
    protected void onStart() {
        super.onStart();

        boolean hasTrailers = mTrailersAdapter.getItemCount() > 0;
        mTrailersLabel.setVisibility(hasTrailers ? View.VISIBLE : View.INVISIBLE);

        boolean hasReviews = mReviewsAdapter.getItemCount() > 0;
        mReviewsLabel.setVisibility(hasReviews ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_MOVIE, mMovie);
        outState.putSerializable(ARG_TRAILERS, mMovieTrailers);
        outState.putParcelable(ARG_REVIEWS, mMovieReviewPage);
    }

    @Override
    protected void onDestroy() {
        if (mTrailersCall != null) {
            mTrailersCall.cancel();
            mTrailersCall = null;
        }
        if (mReviewsCall != null) {
            mReviewsCall.cancel();
            mReviewsCall = null;
        }

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);

        MenuItem favoriteItem = menu.findItem(R.id.action_favorite);
        List<Movie> allMovies = mMovieViewModel.getFavoriteMovies().getValue();
        mIsFavorite = false;
        if (allMovies != null) {
            for (Movie movie: allMovies) {
                if (movie.getId() == mMovie.getId()) {
                    mIsFavorite = true;
                    break;
                }
            }
        }
        favoriteItem.setIcon(mIsFavorite ? R.drawable.ic_star_24dp : R.drawable.ic_star_border_24dp);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorite:
                mMovieViewModel.setFavorite(mMovie, !mIsFavorite);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadTrailers(@NonNull Bundle bundle) {
        if (bundle.containsKey(ARG_TRAILERS)) {
            mMovieTrailers = (MovieTrailers) bundle.getSerializable(ARG_TRAILERS);
            if (mMovieTrailers !=  null)
                refreshTrailers(mMovieTrailers.trailerList);
        }
        else {
            mTrailersCall = MovieNetworkService.getInstance().loadTrailers(mMovie.getId(), response -> {
                // Ignoring paging for now
                mMovieTrailers = response;
                if (response == null) {
                    Toast.makeText(this, R.string.error_network, Toast.LENGTH_SHORT).show();
                }
                refreshTrailers(response != null ? mMovieTrailers.trailerList : null);
                mTrailersCall = null;
            });
        }
    }

    public void loadReviews(@NonNull Bundle bundle) {
        if (bundle.containsKey(ARG_REVIEWS)) {
            mMovieReviewPage = bundle.getParcelable(ARG_REVIEWS);
            if (mMovieReviewPage !=  null)
                refreshReviews(mMovieReviewPage.reviewList);
        }
        else {
            mReviewsCall = MovieNetworkService.getInstance().loadReviews(mMovie.getId(), response -> {
                // Ignoring paging for now
                mMovieReviewPage = response;
                if (response == null) {
                    Toast.makeText(this, R.string.error_network, Toast.LENGTH_SHORT).show();
                }
                refreshReviews(response != null ? mMovieReviewPage.reviewList : null);
                mReviewsCall = null;
            });
        }
    }

    private void loadPoster() {
        Resources resources = getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        int orientation = resources.getConfiguration().orientation;
        int constraintWidth = (orientation == Configuration.ORIENTATION_PORTRAIT) ?
                displayMetrics.widthPixels : displayMetrics.widthPixels / 2;

        String posterSize = ImageUtils.getPosterWidthForConstraint(this, constraintWidth);

        Log.v(TAG, String.format("displayMetrics.widthPixels=%d, displayMetrics.heightPixels=%d",
                displayMetrics.widthPixels, displayMetrics.heightPixels));
        Log.d(TAG, String.format("Orientation: %d, ConstraintWidth: %d, PosterSize: %s",
                orientation,
                constraintWidth,
                posterSize));

        String url = ImageUtils.getUrl(posterSize, mMovie.getPosterUrl());

        Picasso.get().load(url).into(mPosterImage, new Callback() {
            @Override
            public void onSuccess() {
                mProgressBar.setVisibility(View.GONE);
                mContentGroup.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Picasso load error for " + mMovie.getTitle(), e);
                Toast.makeText(MovieDetailsActivity.this, R.string.error_network, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void refreshTrailers(List<MovieTrailers.TrailerInfo> trailers) {
        boolean isEmpty = trailers == null || trailers.isEmpty();
        mTrailersLabel.setVisibility(isEmpty ? View.INVISIBLE : View.VISIBLE);
        mTrailersAdapter.setList(isEmpty ? Collections.emptyList() : trailers);
    }

    private void refreshReviews(List<MovieReviewPage.ReviewInfo> reviews) {
        boolean isEmpty = reviews == null || reviews.isEmpty();
        mReviewsLabel.setVisibility(isEmpty ? View.INVISIBLE : View.VISIBLE);
        mReviewsAdapter.setList(reviews);
    }

}
