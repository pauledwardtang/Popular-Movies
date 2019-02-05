package io.phatcat.popmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class MovieReviewPage implements Parcelable {
    public int id;
    public int page;
    public ArrayList<ReviewInfo> reviewList = new ArrayList<>();
    public int totalPages;
    public int totalResults;

    public MovieReviewPage() {}

    private MovieReviewPage(Parcel in) {
        id = in.readInt();
        page = in.readInt();
        reviewList = (ArrayList<ReviewInfo>) in.readSerializable();
        totalPages = in.readInt();
        totalResults = in.readInt();
    }

    public static final Creator<MovieReviewPage> CREATOR = new Creator<MovieReviewPage>() {
        @Override
        public MovieReviewPage createFromParcel(Parcel in) {
            return new MovieReviewPage(in);
        }

        @Override
        public MovieReviewPage[] newArray(int size) {
            return new MovieReviewPage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(page);
        dest.writeSerializable(reviewList);
        dest.writeInt(totalPages);
        dest.writeInt(totalResults);
    }

    public static class ReviewInfo implements Serializable {
        public String author;
        public String content;
        public String id;
        public String url;
    }

}
