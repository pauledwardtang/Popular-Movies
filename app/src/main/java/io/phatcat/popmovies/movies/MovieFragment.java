package io.phatcat.popmovies.movies;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindDimen;
import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.phatcat.popmovies.MovieSortType;
import io.phatcat.popmovies.R;
import io.phatcat.popmovies.model.Movie;
import io.phatcat.popmovies.network.MovieNetworkService;
import io.phatcat.popmovies.network.OnPostExecuteListener;
import io.phatcat.popmovies.storage.MovieViewModel;
import io.phatcat.popmovies.utils.PreferencesUtils;
import io.phatcat.popmovies.utils.view.CenteredGridSpacingItemDecoration;

import static androidx.recyclerview.widget.RecyclerView.ItemDecoration;

/**
 * A fragment representing a grid of movies.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
// Ignore ButterKnife access warnings, fields cannot be private.
@SuppressWarnings("WeakerAccess")
public class MovieFragment extends Fragment implements OnPostExecuteListener<List<Movie>>,
        SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String ARG_FILTER = "ARG_FILTER";

    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout mRefreshLayout;
    @BindView(android.R.id.list) RecyclerView mRecyclerView;
    @BindView(R.id.noMoviesView) ViewGroup mEmptyView;

    // Binding dimensions to calculate grid size.
    @BindDimen(R.dimen.grid_item_spacing) int mPosterSpacing;
    @BindInt(R.integer.default_grid_span) int mDefaultGridSpan;

    private Unbinder mUnbinder;
    private OnListFragmentInteractionListener mListener;
    private MovieRecyclerViewAdapter mAdapter;
    private MovieSortType mSortType = MovieSortType.POPULAR;
    private MovieViewModel mMovieViewModel;

    /**
     * Required empty public constructor
     */
    public MovieFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        if (savedInstanceState != null) {
            mSortType = (MovieSortType) savedInstanceState.getSerializable(ARG_FILTER);
        }

        mRefreshLayout.setOnRefreshListener(() -> loadMovies(mSortType));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), getSpanSize());
        gridLayoutManager.setSmoothScrollbarEnabled(true);

        mAdapter = new MovieRecyclerViewAdapter(Collections.emptyList(), mListener, getSpanSize());

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        ItemDecoration decoration = new CenteredGridSpacingItemDecoration(getSpanSize(), mPosterSpacing);
        mRecyclerView.addItemDecoration(decoration);

        setHasOptionsMenu(true);

        // Setup network
        mMovieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        mMovieViewModel.getFavoriteMovies().observe(this, movies -> {
            // Refresh favorite movies
            if (mSortType == MovieSortType.FAVORITE) {
                refreshMovies(movies, false);
            }
        });

        MovieNetworkService.getInstance().addListener(this);
        loadMovies(mSortType);

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ARG_FILTER, mSortType);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (PreferencesUtils.PREF_KEY_SORT_FILTER.equals(key)) {
            MovieSortType newSortType = PreferencesUtils.getMovieFilter(sharedPreferences);
            if (newSortType == null) return;

            mSortType = newSortType;
            mRecyclerView.setVisibility(View.INVISIBLE);
            loadMovies(mSortType);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        MovieNetworkService.getInstance().removeListeners();
        mListener = null;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    private int getSpanSize() {
        return mDefaultGridSpan;
    }

    private void loadMovies(@NonNull MovieSortType sortType) {
        mRefreshLayout.setRefreshing(true);
        MovieNetworkService.getInstance().cancelLoad();
        switch (sortType) {
            case POPULAR:
            case TOP_RATED:
                MovieNetworkService.getInstance().loadMovies(sortType);
                break;

            case FAVORITE:
                List<Movie> movies = mMovieViewModel.getFavoriteMovies().getValue();
                refreshMovies(movies, false);
                break;
        }

    }

    @Override
    public void onPostExecute(@Nullable List<Movie> response) {
        refreshMovies(response, true);
    }

    private void refreshMovies(@Nullable List<Movie> movies, boolean shouldCache) {
        if (movies == null) {
            Toast.makeText(requireActivity(), R.string.error_network, Toast.LENGTH_LONG).show();
            mRecyclerView.setVisibility(View.INVISIBLE);
            mEmptyView.setVisibility(View.VISIBLE);
        }
        else if (movies.isEmpty()) {
            // Show the no movies found error
            mRecyclerView.setVisibility(View.INVISIBLE);
            mEmptyView.setVisibility(View.VISIBLE);
        }
        else {
            // Caching all queried movies
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.INVISIBLE);

            if (shouldCache) {
                mMovieViewModel.insertAll(movies);
            }
            mAdapter.setList(movies);
        }

        mRefreshLayout.setRefreshing(false);
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Movie item);
    }
}
