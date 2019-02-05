package io.phatcat.popmovies.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.Nullable;

public class IntentUtils {

    // Credit for YouTube app URI scheme:
    // https://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent
    @Nullable
    public static Intent getYouTubeAppIntent(Context context, String key) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
        return (intent.resolveActivity(context.getPackageManager()) == null) ? null : intent;
    }

    public static Intent getYouTubeWebIntent(String key) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + key));
    }
}
