package io.phatcat.popmovies.moviedetails;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.phatcat.popmovies.R;
import io.phatcat.popmovies.model.Movie;
import io.phatcat.popmovies.utils.ImageUtils;

// Ignore ButterKnife access warnings, fields cannot be private.
@SuppressWarnings("WeakerAccess")
public class MovieDetailsActivity extends AppCompatActivity {
    private static final String TAG = MovieDetailsActivity.class.getSimpleName();
    public static final String ARG_MOVIE = "ARG_MOVIE";

    @BindView(R.id.progressBar) ProgressBar mProgressBar;
    @BindView(R.id.posterImage) ImageView mPosterImage;
    @BindView(R.id.contentGroup) Group mContentGroup;
    @BindView(R.id.synopsisTv) TextView mSynopsisTv;
    @BindView(R.id.userRatingTv) TextView mUserRatingTv;
    @BindView(R.id.releaseDateTv) TextView mReleaseDateTv;

    private Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        mMovie = (savedInstanceState == null) ?
                getIntent().getParcelableExtra(ARG_MOVIE) : savedInstanceState.getParcelable(ARG_MOVIE);

        if (mMovie == null) {
            Log.e(TAG, "No movie specified");
            Toast.makeText(this, R.string.error_no_movie_detail, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        setTitle(mMovie.getTitle());
        mSynopsisTv.setText(mMovie.getSynopsis());
        mUserRatingTv.setText(String.valueOf(mMovie.getRating()));
        mReleaseDateTv.setText(mMovie.getReleaseDate());

        // Load poster
        mContentGroup.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        loadPoster();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_MOVIE, mMovie);
    }

    private void loadPoster() {
        Resources resources = getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        int orientation = resources.getConfiguration().orientation;
        int constraintWidth = (orientation == Configuration.ORIENTATION_PORTRAIT) ?
                displayMetrics.widthPixels : displayMetrics.heightPixels / 4;

        String posterSize = ImageUtils.getPosterWidthForConstraint(this, constraintWidth);
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
                finish();
            }
        });
    }
}
