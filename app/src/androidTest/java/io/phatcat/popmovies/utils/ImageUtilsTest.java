package io.phatcat.popmovies.utils;

import android.content.Context;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static io.phatcat.popmovies.utils.ImageUtils.getPosterWidthForConstraint;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class ImageUtilsTest {

    private final Context context = ApplicationProvider.getApplicationContext();

    @Test
    public void test_getPosterWidthForConstraint() {
        String resolvedWidth = getPosterWidthForConstraint(context, 540);
        assertThat(resolvedWidth, is("w500"));

        resolvedWidth = getPosterWidthForConstraint(context, 500);
        assertThat(resolvedWidth, is("w500"));


        resolvedWidth = getPosterWidthForConstraint(context, 499);
        assertThat(resolvedWidth, not("w500"));
        assertThat(resolvedWidth, is("w342"));
    }
}
