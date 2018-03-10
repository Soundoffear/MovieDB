package com.example.soundoffear.moviedb.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class MovieDataSaR implements Parcelable {

    private String imageURL;
    private String imageID;
    private String imageName;
    private List<String> movieID;
    private String titleMovie;
    private String releaseDate;
    private String moviePoster;
    private String avgVote;
    private String moviePlot;

    public MovieDataSaR(String imageURL, String imageID, String imageName, List<String> movieID
    , String titleMovie, String releaseDate, String moviePoster, String avgVote, String moviePlot) {
        this.imageURL = imageURL;
        this.imageID = imageID;
        this.imageName = imageName;
        this.movieID = movieID;
        this.titleMovie = titleMovie;
        this.releaseDate = releaseDate;
        this.moviePoster = moviePoster;
        this.avgVote = avgVote;
        this.moviePlot = moviePlot;
    }

    protected MovieDataSaR(Parcel in) {
        imageURL = in.readString();
        imageID = in.readString();
        imageName = in.readString();
        in.readStringList(movieID);
        titleMovie = in.readString();
        releaseDate = in.readString();
        moviePoster = in.readString();
        avgVote = in.readString();
        moviePlot = in.readString();
    }

    public static final Creator<MovieDataSaR> CREATOR = new Creator<MovieDataSaR>() {
        @Override
        public MovieDataSaR createFromParcel(Parcel in) {
            return new MovieDataSaR(in);
        }

        @Override
        public MovieDataSaR[] newArray(int size) {
            return new MovieDataSaR[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageURL);
        dest.writeString(imageID);
        dest.writeString(imageName);
        dest.writeStringList(movieID);
        dest.writeString(titleMovie);
        dest.writeString(releaseDate);
        dest.writeString(moviePoster);
        dest.writeString(avgVote);
        dest.writeString(moviePlot);
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getImageID() {
        return imageID;
    }

    public String getImageName() {
        return imageName;
    }

    public List<String> getMovieID() {
        return movieID;
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
}
