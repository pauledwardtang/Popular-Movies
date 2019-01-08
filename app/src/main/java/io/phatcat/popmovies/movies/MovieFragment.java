package io.phatcat.popmovies.movies;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import io.phatcat.popmovies.network.MoviesAsyncTask;
import io.phatcat.popmovies.network.OnPostExecuteListener;
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
public class MovieFragment extends Fragment implements OnPostExecuteListener<List<Movie>> {

    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout mRefreshLayout;
    @BindView(android.R.id.list) RecyclerView mRecyclerView;
    @BindView(android.R.id.empty) TextView mEmptyView;

    // Binding dimensions to calculate grid size.
    @BindDimen(R.dimen.grid_item_spacing) int mPosterSpacing;
    @BindInt(R.integer.default_grid_span) int mDefaultGridSpan;

    private Unbinder mUnbinder;
    private OnListFragmentInteractionListener mListener;
    private MovieRecyclerViewAdapter mAdapter;
    private MovieSortType mSortType = MovieSortType.POPULAR;

    /**
     * Required empty public constructor
     */
    public MovieFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        mRefreshLayout.setOnRefreshListener(() -> loadMovies(mSortType));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), getSpanSize());
        gridLayoutManager.setSmoothScrollbarEnabled(true);

        mAdapter = new MovieRecyclerViewAdapter(Collections.emptyList(), mListener, getSpanSize());
        mEmptyView.setVisibility(View.VISIBLE);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        ItemDecoration decoration = new CenteredGridSpacingItemDecoration(getSpanSize(), mPosterSpacing);
        mRecyclerView.addItemDecoration(decoration);

        setHasOptionsMenu(true);
        loadMovies(mSortType);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem spinnerItem = menu.findItem(R.id.action_bar_spinner);
        Spinner spinner = (Spinner) spinnerItem.getActionView();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireActivity(),
                R.array.sort_filters,
                android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSortType = (position == 0) ? MovieSortType.POPULAR : MovieSortType.TOP_RATED;
                loadMovies(mSortType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    private int getSpanSize() {
        return mDefaultGridSpan;
    }

    private void loadMovies(MovieSortType sortType) {
        mRefreshLayout.setRefreshing(true);
        new MoviesAsyncTask(this).execute(sortType);
    }

    @Override
    public void onPostExecute(@Nullable List<Movie> response) {
        if (response == null) {
            Toast.makeText(requireActivity(), R.string.error_network, Toast.LENGTH_LONG).show();
        }
        else if (response.isEmpty()) {
            Toast.makeText(requireActivity(), R.string.movie_list_empty, Toast.LENGTH_LONG).show();
        }
        else {
            mAdapter.setList(response);
        }
        mRefreshLayout.setRefreshing(false);
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Movie item);
    }
}