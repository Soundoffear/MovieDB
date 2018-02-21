package com.example.soundoffear.moviedb;

import android.content.Intent;
import android.graphics.Movie;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.soundoffear.moviedb.model.MovieData;
import com.example.soundoffear.moviedb.utilities.NetworkUtilities;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    TextView movie_title;
    TextView release_date;
    TextView average_vote;
    TextView movie_plot;

    ImageView movie_poster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Intent intent = getIntent();

        movie_title = findViewById(R.id.detail_title_value);
        release_date = findViewById(R.id.detail_release_date_value);
        average_vote = findViewById(R.id.detail_vote_value);
        movie_plot = findViewById(R.id.detail_plot);

        movie_poster = findViewById(R.id.detail_movie_poster);

        MovieData movieData = (MovieData) intent.getSerializableExtra(MainActivity.MOVIE_DETAIL);

        movie_title.setText(movieData.getTitleMovie());
        release_date.setText(movieData.getReleaseDate());
        average_vote.setText(movieData.getAvgVote());
        movie_plot.setText(movieData.getMoviePlot());

        String test = NetworkUtilities.buildImageBigURL(movieData.getMoviePoster()).toString();
        Log.d("test img", test);

        Picasso.with(getApplicationContext()).load(test).into(movie_poster);
    }
}
