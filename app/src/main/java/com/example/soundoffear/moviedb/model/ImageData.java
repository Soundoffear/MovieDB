package com.example.soundoffear.moviedb.model;

import java.util.List;

/**
 * Created by soundoffear on 03/03/2018.
 */

public class ImageData {

    private String imageURL;
    private String imageID;
    private String imageName;
    private List<String> movieID;

    public ImageData(String imageURL, String imageID, List<String> movieID, String imageName) {
        this.imageURL = imageURL;
        this.imageID = imageID;
        this.movieID = movieID;
        this.imageName = imageName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getImageID() {
        return imageID;
    }

    public List<String> getMovieID() {
        return movieID;
    }

    public String getImageName() {
        return imageName;
    }
}
