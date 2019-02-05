package io.phatcat.popmovies.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MovieTrailers implements Serializable {
    public int id;

    /**
     * Sourced from YouTube, not Quicktime
     */
    public List<TrailerInfo> trailerList = new ArrayList<>();

    public static class TrailerInfo implements Serializable {
        public String name;

        // E.g. for YouTube this would be the ID for the video
        public String key;

        // E.g. YouTube or elsewhere
        public String site;

        // Allowed Values: 360, 480, 720, 1080 (HD also observed)
        public String size;

        // Ex. Teaser, MovieTrailers, Featurette, Clip
        // Allowed Values: Trailer, Teaser, Clip, Featurette
        public String type;
    }

}
