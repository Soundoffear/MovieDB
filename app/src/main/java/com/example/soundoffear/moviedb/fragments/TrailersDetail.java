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
import com.example.soundoffear.moviedb.adapters.TrailersRecViewAdapter;
import com.example.soundoffear.moviedb.model.TrailerData;
import com.example.soundoffear.moviedb.utilities.JSONUtilities;
import com.example.soundoffear.moviedb.utilities.NetworkUtilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by soundoffear on 26/02/2018.
 */

public class TrailersDetail extends Fragment {

    RecyclerView trailersRV;

    TrailersRecViewAdapter trailersRecViewAdapter;
    List<TrailerData> trailerDataList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View trailersView = inflater.inflate(R.layout.trailer_detail_fragment, container, false);

        trailerDataList = new ArrayList<>();

        trailersRV = trailersView.findViewById(R.id.trailers_recyclerView);
        trailersRV.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        trailersRV.setLayoutManager(linearLayoutManager);
        trailersRecViewAdapter = new TrailersRecViewAdapter(getContext(), trailerDataList);
        trailersRV.setAdapter(trailersRecViewAdapter);

        new RunTrailerAsync().execute(MovieDetailActivity.movieID);

        return trailersView;

    }

    class RunTrailerAsync extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            try {
                String jsonTrailer = NetworkUtilities.downloadTrailerJSON(NetworkUtilities.getYouTubeKey(strings[0]));

                trailerDataList = JSONUtilities.trailersKeysList(jsonTrailer);

            } catch (Exception e) {

            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
                trailersRecViewAdapter = new TrailersRecViewAdapter(getContext(), trailerDataList);
                trailersRV.setAdapter(trailersRecViewAdapter);
        }
    }
}
