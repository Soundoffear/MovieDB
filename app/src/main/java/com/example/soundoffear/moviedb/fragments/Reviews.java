package com.example.soundoffear.moviedb.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.soundoffear.moviedb.MovieDetailActivity;
import com.example.soundoffear.moviedb.R;
import com.example.soundoffear.moviedb.adapters.ReviewRecViewAdapter;
import com.example.soundoffear.moviedb.model.ReviewData;
import com.example.soundoffear.moviedb.utilities.JSONUtilities;
import com.example.soundoffear.moviedb.utilities.NetworkUtilities;

import java.util.ArrayList;
import java.util.List;

public class Reviews extends Fragment {

    List<ReviewData> reviewDataList;
    RecyclerView reviewRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View viewReviews = inflater.inflate(R.layout.review_detail_fragment, container, false);

        reviewDataList = new ArrayList<>();
        reviewRecyclerView = viewReviews.findViewById(R.id.review_recyclerView);
        reviewRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        reviewRecyclerView.setLayoutManager(linearLayoutManager);
        ReviewRecViewAdapter reviewRecViewAdapter = new ReviewRecViewAdapter(reviewDataList);
        reviewRecyclerView.setAdapter(reviewRecViewAdapter);

        new RunReviewFetchAsync().execute(MovieDetailActivity.movieID);

        return viewReviews;

    }

    class RunReviewFetchAsync extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                String reviewsJSON = NetworkUtilities.downloadJSON(NetworkUtilities.getReviews(strings[0]));

                reviewDataList = JSONUtilities.allReviewsOfMovie(reviewsJSON);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ReviewRecViewAdapter reviewRecViewAdapter = new ReviewRecViewAdapter(reviewDataList);
            reviewRecyclerView.setAdapter(reviewRecViewAdapter);
        }
    }
}
