package io.phatcat.popmovies;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.phatcat.popmovies.model.Movie;
import io.phatcat.popmovies.moviedetails.MovieDetailsActivity;
import io.phatcat.popmovies.movies.MovieFragment;
import io.phatcat.popmovies.utils.IntentUtils;
import io.phatcat.popmovies.utils.PreferencesUtils;

import static io.phatcat.popmovies.moviedetails.MovieDetailsActivity.ARG_MOVIE;

public class MainActivity extends AppCompatActivity
        implements MovieFragment.OnListFragmentInteractionListener,
        BottomNavigationView.OnNavigationItemSelectedListener {
    private static final String ARG_TITLE = "title_arg";

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.toolbarLayout) CollapsingToolbarLayout mToolbarLayout;
    @BindView(R.id.navigation) BottomNavigationView mBottomNavView;
    @BindView(R.id.title) TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Setup Toolbar
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setIcon(R.mipmap.ic_launcher);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME);
        }

        if (savedInstanceState != null) {
            mTitle.setText(savedInstanceState.getString(ARG_TITLE));
        }
        else {
            mTitle.setText(R.string.title_popular);
        }

        // Setup navigation
        mBottomNavView.setOnNavigationItemSelectedListener(this);

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_TITLE, mTitle.getText().toString());
    }

    @Override
    public void onListFragmentInteraction(Movie item) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra(ARG_MOVIE, item);

        //noinspection unchecked Shared elements (such as the poster image) will be added here.
        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle();
        startActivity(intent, bundle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                Intent intent = Intent.createChooser(IntentUtils.getPrivacyPolicyIntent(),
                        getString(R.string.title_privacy_policy));

                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Force a refresh in listeners.
        PreferencesUtils.clearMovieFilter(this);

        switch (item.getItemId()) {
            case R.id.popularity:
                mTitle.setText(R.string.title_popular);
                PreferencesUtils.setMovieFilter(this, MovieSortType.POPULAR);
                break;

            case R.id.top_rated:
                mTitle.setText(R.string.title_top_rated);
                PreferencesUtils.setMovieFilter(this, MovieSortType.TOP_RATED);
                break;

            case R.id.favorites:
                mTitle.setText(R.string.title_favorite);
                PreferencesUtils.setMovieFilter(this, MovieSortType.FAVORITE);
                break;
        }

        return true;
    }

}
