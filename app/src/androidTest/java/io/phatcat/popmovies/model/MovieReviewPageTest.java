package io.phatcat.popmovies.model;

import android.os.Bundle;
import android.os.Parcel;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Test data marshalling
 */
public class MovieReviewPageTest {
    private MovieReviewPage.ReviewInfo reviewInfo;

    @Before
    public void setUp() {
        reviewInfo = new MovieReviewPage.ReviewInfo();
        reviewInfo.author = "Author";
        reviewInfo.content = "Content";
        reviewInfo.id = "ASDFJKL";
        reviewInfo.url = "http://google.com/";
    }

    @Test
    public void readWriteSingleReview() {
        MovieReviewPage subject = new MovieReviewPage();
        subject.id = 1;
        subject.page = 2;
        subject.totalPages = 1;
        subject.totalResults = 1;
        subject.reviewList = new ArrayList<>();
        subject.reviewList.add(reviewInfo);

        Bundle bundle = new Bundle();
        bundle.putParcelable("test", subject);

        MovieReviewPage actual = bundle.getParcelable("test");

        assertNotNull(actual);
        assertEquals(subject.id, actual.id);
        assertEquals(subject.page, actual.page);
        assertEquals(subject.totalPages, actual.totalPages);
        assertEquals(subject.totalResults, actual.totalResults);
        assertEquals(subject.reviewList.size(), actual.reviewList.size());

        assertEquals(reviewInfo.author, actual.reviewList.get(0).author);
        assertEquals(reviewInfo.content, actual.reviewList.get(0).content);
        assertEquals(reviewInfo.id, actual.reviewList.get(0).id);
        assertEquals(reviewInfo.url, actual.reviewList.get(0).url);
    }

    @Test
    public void readWriteEmptyReviews() {
        MovieReviewPage subject = new MovieReviewPage();

        Bundle bundle = new Bundle();
        bundle.putParcelable("test", subject);

        MovieReviewPage actual = bundle.getParcelable("test");

        assertNotNull(actual);
        assertNotNull(actual.reviewList);
        assertEquals(0, actual.reviewList.size());
    }

    @Test
    public void readWriteMultipleReviews() {
        MovieReviewPage subject = new MovieReviewPage();
        subject.reviewList = new ArrayList<>();
        subject.reviewList.add(reviewInfo);
        subject.reviewList.add(reviewInfo);

        Bundle bundle = new Bundle();
        bundle.putParcelable("test", subject);

        MovieReviewPage actual = bundle.getParcelable("test");

        assertNotNull(actual);
        assertEquals(2, actual.reviewList.size());
        assertEquals(subject.reviewList.size(), actual.reviewList.size());
    }
}
