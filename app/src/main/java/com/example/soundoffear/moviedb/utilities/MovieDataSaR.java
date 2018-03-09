package com.example.soundoffear.moviedb.utilities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class MovieDataSaR implements Parcelable {

    private String imageURL;
    private String imageID;
    private String imageName;
    private List<String> movieID;

    public MovieDataSaR(String imageURL, String imageID, String imageName, List<String> movieID) {
        this.imageURL = imageURL;
        this.imageID = imageID;
        this.imageName = imageName;
        this.movieID = movieID;
    }

    protected MovieDataSaR(Parcel in) {
        imageURL = in.readString();
        imageID = in.readString();
        imageName = in.readString();
        in.readStringList(movieID);
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
}
