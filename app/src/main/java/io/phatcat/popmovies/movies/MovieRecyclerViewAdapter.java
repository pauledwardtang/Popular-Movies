package io.phatcat.popmovies.movies;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.DrawableUtils;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.phatcat.popmovies.R;
import io.phatcat.popmovies.model.Movie;
import io.phatcat.popmovies.movies.MovieFragment.OnListFragmentInteractionListener;
import io.phatcat.popmovies.utils.ImageUtils;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Movie} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MovieRecyclerViewAdapter extends RecyclerView.Adapter<MovieRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = MovieRecyclerViewAdapter.class.getSimpleName();
    private List<Movie> mValues;
    private final OnListFragmentInteractionListener mListener;
    private String mPosterSize;
    private final int mGridSize;
    private Drawable mPlaceholderDrawable;

    public MovieRecyclerViewAdapter(List<Movie> items,
                                    OnListFragmentInteractionListener listener,
                                    int gridSize) {
        if (gridSize < 2) {
            throw new IllegalStateException();
        }

        mValues = items;
        mListener = listener;
        mGridSize = gridSize;
    }

    @Override
    public long getItemId(int position) {
        return (long) mValues.get(position).getId();
    }

    @Override
    public int getItemViewType(int position) { return R.layout.movie_card_item; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Movie movie = mValues.get(position);
        String url = ImageUtils.getUrl(mPosterSize, movie.getPosterUrl());
        Picasso.get()
                .load(url)
                .noFade()
                .error(R.drawable.grid_poster_placeholder)
                .placeholder(mPlaceholderDrawable)
                .into(holder.posterImage);

        holder.mMovie = movie;
        holder.itemView.setOnClickListener(v -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onListFragmentInteraction(holder.mMovie);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void setList(List<Movie> movies) {
        mValues = movies;
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        int deviceWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        int widthScalarForGridSize = deviceWidth / (mGridSize - 1); // Get a slightly larger image

        mPosterSize =
                ImageUtils.getPosterWidthForConstraint(recyclerView.getContext(), widthScalarForGridSize);
        Log.d(TAG, "Resolved poster size for grid: " + mPosterSize);

        mPlaceholderDrawable = recyclerView.getContext().getDrawable(R.drawable.grid_poster_placeholder);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.posterImage) AppCompatImageView posterImage;
        private Movie mMovie;

        private ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
