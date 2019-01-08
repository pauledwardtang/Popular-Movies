package io.phatcat.popmovies.utils;

import android.content.Context;

import io.phatcat.popmovies.R;

public final class ImageUtils {
    private static final String SERVICE_BASE_URL = "http://image.tmdb.org/t/p/";

    // Don't allow instantiation
    private ImageUtils() {}

    public static String getUrl(String posterSize, String path) {
        return SERVICE_BASE_URL + posterSize + path;
    }

    /**
     * Gets the maximum poster width that fits in the given constraint.
     */
    public static String getPosterWidthForConstraint(Context context, int maxWidth) {
        String[] posterSizes = context.getResources().getStringArray(R.array.poster_sizes);
        int[] posterDimens = context.getResources().getIntArray(R.array.poster_widths_px);

        // Early out at the largest dimension found that fits within the constraint.
        for (int i = posterDimens.length - 1; i >= 0; i--) {
            if (posterDimens[i] <= maxWidth) {
                return posterSizes[i];
            }
        }
        throw new IllegalArgumentException("Invalid width: " + maxWidth);
    }
}
