package com.example.soundoffear.moviedb.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.soundoffear.moviedb.MovieDetailActivity;
import com.example.soundoffear.moviedb.R;

/**
 * Created by soundoffear on 26/02/2018.
 */

public class PlotDetail extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View plotView = inflater.inflate(R.layout.plot_detail_fragment, container, false);

        TextView plot = plotView.findViewById(R.id.plot_fragment_input);

        String getPlot = getArguments().getString(MovieDetailActivity.PLOT_DETAIL);

        plot.setText(getPlot);

        return plotView;
    }
}
