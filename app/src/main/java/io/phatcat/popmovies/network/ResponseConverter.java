package io.phatcat.popmovies.network;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import okhttp3.ResponseBody;
import retrofit2.Converter;

public class ResponseConverter<T> implements Converter<ResponseBody, T> {

    private final Parser<T> mParser;

    ResponseConverter(Parser<T> parser) {
        mParser = parser;
    }

    @Nullable
    @Override
    public T convert(@NonNull ResponseBody value) {
        try {
            return mParser.parse(value.string());
        }
        catch (IOException | OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }
}
