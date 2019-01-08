package io.phatcat.popmovies.utils.view;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * This class adjusts the view bounds of grid items.
 */
public final class CenteredGridSpacingItemDecoration extends RecyclerView.ItemDecoration {
    private final int mNumColumns;
    private final int mSpacing;


    public CenteredGridSpacingItemDecoration(int numColumns, int spacing) {
        mNumColumns = numColumns;
        mSpacing = spacing;
    }

    /**
     * Extra calculations were done here to adjust margins on each item. For some reason, the
     * RecyclerView and GridLayoutManager don't have an auto-fit feature (at time of writing).
     * See:
     * https://stackoverflow.com/questions/28531996/android-recyclerview-gridlayoutmanager-column-spacing
     */
    @Override
    public void getItemOffsets(@NonNull Rect outRect,
                               @NonNull View view,
                               @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int numColumns = position % mNumColumns;

        outRect.left = mSpacing - numColumns * mSpacing / mNumColumns;
        outRect.right = (numColumns + 1) * mSpacing / mNumColumns;
        outRect.bottom = mSpacing;

        if (position < mNumColumns) {
            outRect.top = mSpacing;
        }
    }
}
