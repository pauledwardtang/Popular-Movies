package io.phatcat.popmovies.utils;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import io.phatcat.popmovies.BuildConfig;

public final class NetworkUtils {
    private static final String SERVICE_BASE_URL = "http://api.themoviedb.org/3/movie";
    private static final String PARAM_API_KEY = "api_key";
    public static final String PATH_POPULAR_MOVIES = "popular";
    public static final String PATH_TOP_RATED_MOVIES = "top_rated";

    public static URL buildUrl(String query) {
        Uri uri = Uri.parse(SERVICE_BASE_URL).buildUpon()
                .appendPath(query)
                .appendQueryParameter(PARAM_API_KEY, BuildConfig.API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
