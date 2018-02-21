package com.example.soundoffear.moviedb.utilities;

import android.util.Log;

import com.example.soundoffear.moviedb.model.MovieData;

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
                    MovieData movieData = new MovieData(movie_title, release_date, movie_poster, avg_vote, plot);
                    movieDataList.add(movieData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return movieDataList;
    }

    public static List<String> imageString(String s) {
        List<String> imageString = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            Log.d("JSONARRAY", jsonArray.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject image = jsonArray.getJSONObject(i);
                imageString.add(image.getString("poster_path"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imageString;
    }

}
