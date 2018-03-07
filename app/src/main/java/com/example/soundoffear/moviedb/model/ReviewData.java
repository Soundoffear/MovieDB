package com.example.soundoffear.moviedb.model;

/**
 * Created by soundoffear on 28/02/2018.
 */

public class ReviewData {

    private String author;
    private String review;

    public ReviewData(String author, String review) {
        this.author = author;
        this.review = review;
    }

    public String getAuthor() {
        return author;
    }

    public String getReview() {
        return review;
    }
}
