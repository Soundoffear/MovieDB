package com.example.soundoffear.moviedb.utilities;

import android.util.Log;

import com.example.soundoffear.moviedb.model.MovieData;
import com.example.soundoffear.moviedb.model.ReviewData;
import com.example.soundoffear.moviedb.model.TrailerData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by soundoffear on 20/02/2018.
 */

public class JSONUtilities {

    public static List<MovieData> allMoviesData(String s) {
        List<MovieData> movieDataList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            if(jsonArray != null) {
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    String movie_title = object.getString("title");
                    String release_date = object.getString("release_date");
                    String movie_poster = object.getString("poster_path");
                    String avg_vote = object.getString("vote_average");
                    String plot = object.getString("overview");
                    String movieID = object.getString("id");
                    MovieData movieData = new MovieData(movie_title, release_date, movie_poster, avg_vote, plot, movieID);
                    movieDataList.add(movieData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return movieDataList;
    }

    public static List<ReviewData> allReviewsOfMovie(String s) {

        List<ReviewData> reviewData = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject reviewObj = jsonArray.getJSONObject(i);
                Log.d("review", reviewObj.toString());
                String author = reviewObj.getString("author");
                String review = reviewObj.getString("content");
                ReviewData reviewData1 = new ReviewData(author, review);
                reviewData.add(reviewData1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return reviewData;
    }

    public static List<TrailerData> trailersKeysList(String s) {
        List<TrailerData> trailerDataList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject image = jsonArray.getJSONObject(i);
                String trailerKey = image.getString("key");
                String trailerName = image.getString("name");
                TrailerData trailerData = new TrailerData(trailerKey, trailerName);
                trailerDataList.add(trailerData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return trailerDataList;
    }

}