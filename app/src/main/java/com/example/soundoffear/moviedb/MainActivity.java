package com.example.soundoffear.moviedb;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.soundoffear.moviedb.interfaces.OnClickRecView;
import com.example.soundoffear.moviedb.model.MovieData;
import com.example.soundoffear.moviedb.utilities.JSONUtilities;
import com.example.soundoffear.moviedb.utilities.NetworkUtilities;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnClickRecView {

    RecyclerView imagesGrid;
    List<String> stringURIList;
    List<MovieData> movieDataList;
    MovieRecViewAdapter movieRecViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stringURIList = new ArrayList<>();

        movieDataList = new ArrayList<>();

        new RunAsyncConnection().execute();

        imagesGrid = findViewById(R.id.imagesListRecView);

        imagesGrid.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 4);
        imagesGrid.setLayoutManager(gridLayoutManager);

        movieRecViewAdapter = new MovieRecViewAdapter(getApplicationContext(), stringURIList, this);

        imagesGrid.setAdapter(movieRecViewAdapter);

    }

    @Override
    public void onRecyclerViewClick(int position) {

    }

    private String getSortOrderFromPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getString("SORT_ORDER", "Popularity");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_order:
                Intent intent = new Intent(MainActivity.this, SortOrderProperties.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public static final String MOVIE_DETAIL = "movie_detail_data";

    class RunAsyncConnection extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String jsonString;
            try {
                jsonString = NetworkUtilities.downloadJSON(NetworkUtilities.buildURL(getSortOrderFromPreferences()));
                movieDataList.clear();
                stringURIList.clear();
                movieDataList = JSONUtilities.allMoviesData(jsonString);
                if(movieDataList.size() > 0) {
                    for (int i = 0; i < movieDataList.size(); i++) {
                        URL stringURL = NetworkUtilities.buildImageURL(movieDataList.get(i).getMoviePoster());
                        stringURIList.add(stringURL.toString());
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            movieRecViewAdapter = new MovieRecViewAdapter(getApplicationContext(), stringURIList, new OnClickRecView() {
                @Override
                public void onRecyclerViewClick(int position) {
                    String title = movieDataList.get(position).getTitleMovie();
                    Log.d("MOVIE TITLE", title);

                    Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);

                    intent.putExtra(MOVIE_DETAIL, movieDataList.get(position));
                    startActivity(intent);
                }
            });
            imagesGrid.setAdapter(movieRecViewAdapter);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        new RunAsyncConnection().execute();
    }
}
