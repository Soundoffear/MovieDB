package com.example.soundoffear.moviedb.model;

/**
 * Created by soundoffear on 01/03/2018.
 */

public class TrailerData {

    private String trailersKey;
    private String trailersName;

    public TrailerData(String trailersKey, String trailersName) {
        this.trailersKey = trailersKey;
        this.trailersName = trailersName;
    }

    public String getTrailersKey() {
        return trailersKey;
    }

    public String getTrailersName() {
        return trailersName;
    }
}
