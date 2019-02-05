package io.phatcat.popmovies.network;

public interface Parser<T> {
    T parse(String arg);
}
