package io.phatcat.popmovies.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import io.phatcat.popmovies.MovieSortType;

/**
 * Utility class for this app's SharedPreferences.
 */
public final class PreferencesUtils {
    public static final String PREF_KEY_SORT_FILTER = "sort_filter";

    private PreferencesUtils() {}

    public static void setMovieFilter(Context context, MovieSortType sortFilter) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(PREF_KEY_SORT_FILTER, sortFilter.name()).apply();
    }

    public static void clearMovieFilter(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().remove(PREF_KEY_SORT_FILTER).apply();
    }

    public static MovieSortType getMovieFilter(SharedPreferences preferences) {
        String sortFilterName = preferences.getString(PREF_KEY_SORT_FILTER, null);
        return sortFilterName == null ? null : MovieSortType.valueOf(sortFilterName);
    }

}
