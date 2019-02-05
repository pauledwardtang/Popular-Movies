package io.phatcat.popmovies.moviedetails;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.phatcat.popmovies.R;
import io.phatcat.popmovies.model.MovieTrailers;
import io.phatcat.popmovies.utils.IntentUtils;

public class MovieTrailerRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<MovieTrailers.TrailerInfo> mTrailerInfoList;

    /**
     * @param list Initial list
     */
    public MovieTrailerRecyclerViewAdapter(@NonNull List<MovieTrailers.TrailerInfo> list) {
        setList(list);
    }

    @Override
    public int getItemCount() { return (mTrailerInfoList== null) ? 0 : mTrailerInfoList.size(); }

    @Override
    public int getItemViewType(int position) {
        return R.layout.trailer_list_item;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MovieTrailers.TrailerInfo trailerInfo = mTrailerInfoList.get(position);

        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.info = trailerInfo;
        viewHolder.name.setText(trailerInfo.name);
        viewHolder.type.setText(trailerInfo.type);
        viewHolder.size.setText(trailerInfo.size);
        viewHolder.playButton.setOnClickListener(v -> {
            Log.d("ViewHolder", trailerInfo.key);
            Context context = viewHolder.itemView.getContext();
            if (viewHolder.info.site.toLowerCase().equals("YouTube".toLowerCase())) {
                Intent intent = IntentUtils.getYouTubeAppIntent(context, viewHolder.info.key);
                if (intent == null) {
                    intent = IntentUtils.getYouTubeWebIntent(viewHolder.info.key);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    /**
     * Updates the backing data for this adapter.
     * @param list The list for which to update
     */
    public void setList(@NonNull List<MovieTrailers.TrailerInfo> list) {
        mTrailerInfoList = list;
        notifyDataSetChanged();
    }

    public List<MovieTrailers.TrailerInfo> getList() { return mTrailerInfoList; }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private MovieTrailers.TrailerInfo info;

        @BindView(R.id.name) TextView name;
        @BindView(R.id.type) TextView type;
        @BindView(R.id.size) TextView size;
        @BindView(R.id.playButton) ImageButton playButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
