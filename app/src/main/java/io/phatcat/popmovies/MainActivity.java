package io.phatcat.popmovies;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import io.phatcat.popmovies.moviedetails.MovieDetailsActivity;
import io.phatcat.popmovies.model.Movie;
import io.phatcat.popmovies.movies.MovieFragment;

import static io.phatcat.popmovies.moviedetails.MovieDetailsActivity.ARG_MOVIE;

public class MainActivity extends AppCompatActivity
        implements MovieFragment.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setIcon(R.mipmap.ic_launcher);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        }
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
}
