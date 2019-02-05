package io.phatcat.popmovies.moviedetails;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.phatcat.popmovies.R;
import io.phatcat.popmovies.model.MovieReviewPage;

public class MovieReviewRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<MovieReviewPage.ReviewInfo> mReviews;

    public MovieReviewRecyclerViewAdapter(List<MovieReviewPage.ReviewInfo> reviews) {
        mReviews = reviews;
    }

    @Override
    public int getItemCount() { return (mReviews == null) ? 0 : mReviews.size(); }

    @Override
    public int getItemViewType(int position) { return R.layout.review_list_item; }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.reviewInfo = mReviews.get(position);

        viewHolder.author.setText(viewHolder.reviewInfo.author);

        // Content seems to have Markdown formatting, but we won't do anything about it here quite yet.
        viewHolder.content.setText(viewHolder.reviewInfo.content);

        // Set the onClick listener on the whole view if the button is not available
        ((viewHolder.fullReviewButton == null) ?
            viewHolder.itemView : viewHolder.fullReviewButton).setOnClickListener(v -> {
            // Let the user browse the review in it's original format
            Context context = viewHolder.itemView.getContext();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(viewHolder.reviewInfo.url));
            context.startActivity(intent);
        });
    }

    public void setList(List<MovieReviewPage.ReviewInfo> reviews) {
        mReviews = reviews;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        MovieReviewPage.ReviewInfo reviewInfo;
        @BindView(R.id.author) TextView author;
        @BindView(R.id.content) TextView content;
        @Nullable @BindView(R.id.seeReviewButton) Button fullReviewButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
