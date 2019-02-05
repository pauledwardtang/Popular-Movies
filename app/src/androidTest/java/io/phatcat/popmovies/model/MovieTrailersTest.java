package io.phatcat.popmovies.model;

import android.os.Bundle;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MovieTrailersTest {

    private MovieTrailers.TrailerInfo trailerInfo;

    @Before
    public void setUp() {
        trailerInfo = new MovieTrailers.TrailerInfo();
        trailerInfo.name = "Trailer Name";
        trailerInfo.key = "8d8DUDUD";
        trailerInfo.size = "800";
        trailerInfo.site = "YouTube";
        trailerInfo.type = "Clip";
    }

    @Test
    public void marshalSingleTrailer() {
        MovieTrailers subject = new MovieTrailers();
        subject.id = 4;
        subject.trailerList.add(trailerInfo);

        Bundle bundle = new Bundle();
        bundle.putSerializable("test", subject);

        MovieTrailers actual = (MovieTrailers) bundle.getSerializable("test");
        assertNotNull(actual);
        assertNotNull(actual.trailerList);
        assertEquals(subject.id, actual.id);
        assertEquals(1, actual.trailerList.size());
        assertEquals(trailerInfo.name, actual.trailerList.get(0).name);
        assertEquals(trailerInfo.key, actual.trailerList.get(0).key);
        assertEquals(trailerInfo.size, actual.trailerList.get(0).size);
        assertEquals(trailerInfo.site, actual.trailerList.get(0).site);
        assertEquals(trailerInfo.type, actual.trailerList.get(0).type);
    }
}
