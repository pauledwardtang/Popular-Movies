package io.phatcat.popmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "movie")
public class Movie implements Parcelable {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private int mId;

    @ColumnInfo(name = "title")
    private String mTitle;

    @ColumnInfo(name = "poster_path")
    private String mPosterUrl;

    @ColumnInfo(name = "overview")
    private String mSynopsis;

    @ColumnInfo(name = "vote_average")
    private double mRating;

    @ColumnInfo(name = "release_date")
    private String mReleaseDate;

    public Movie() {}

    @Ignore
    private Movie(Parcel in) {
        mId = in.readInt();
        mTitle = in.readString();
        mPosterUrl = in.readString();
        mSynopsis = in.readString();
        mRating = in.readDouble();
        mReleaseDate = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getId());
        dest.writeString(getTitle());
        dest.writeString(getPosterUrl());
        dest.writeString(getSynopsis());
        dest.writeDouble(getRating());
        dest.writeString(getReleaseDate());
    }

    public int getId() { return mId; }

    public void setId(int id) { mId = id; }

    public String getTitle() { return mTitle; }

    public void setTitle(String title) { mTitle = title; }

    public String getPosterUrl() { return mPosterUrl; }

    public void setPosterUrl(String posterUrl) { mPosterUrl = posterUrl; }

    public String getSynopsis() { return mSynopsis; }

    public void setSynopsis(String synopsis) { mSynopsis = synopsis; }

    public double getRating() { return mRating; }

    public void setRating(double rating) { mRating = rating; }

    public String getReleaseDate() { return mReleaseDate; }

    public void setReleaseDate(String releaseDate) { mReleaseDate = releaseDate; }

    @NonNull
    @Override
    public String toString() { return mTitle + " " + mReleaseDate; }
}
