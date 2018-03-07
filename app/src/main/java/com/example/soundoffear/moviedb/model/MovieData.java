package com.example.soundoffear.moviedb.model;

/**
 * Created by soundoffear on 20/02/2018.
 */

import java.io.Serializable;

public class MovieData implements Serializable {

    private String titleMovie;
    private String releaseDate;
    private String moviePoster;
    private String avgVote;
    private String moviePlot;
    private String movieID;

    public MovieData(String titleMovie, String releaseDate, String moviePoster, String avgVote, String moviePlot, String movieID) {
        this.titleMovie = titleMovie;
        this.releaseDate = releaseDate;
        this.moviePoster = moviePoster;
        this.avgVote = avgVote;
        this.moviePlot = moviePlot;
        this.movieID = movieID;
    }

    public String getTitleMovie() {
        return titleMovie;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getMoviePoster() {
        return moviePoster;
    }

    public String getAvgVote() {
        return avgVote;
    }

    public String getMoviePlot() {
        return moviePlot;
    }

    public String getMovieID() {
        return movieID;
    }
}
