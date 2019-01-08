package io.phatcat.popmovies.network;

import androidx.annotation.Nullable;

public interface OnPostExecuteListener<T> {
    void onPostExecute(@Nullable T response);
}
