package com.example.soundoffear.moviedb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.example.soundoffear.moviedb.adapters.MovieRecViewAdapter;
import com.example.soundoffear.moviedb.databases.FavouritesContentProvider;
import com.example.soundoffear.moviedb.databases.FavouritesContract;
import com.example.soundoffear.moviedb.interfaces.OnClickRecView;
import com.example.soundoffear.moviedb.model.ImageData;
import com.example.soundoffear.moviedb.model.MovieData;
import com.example.soundoffear.moviedb.utilities.JSONUtilities;
import com.example.soundoffear.moviedb.utilities.NetworkUtilities;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnClickRecView {

    RecyclerView imagesGrid;
    List<MovieData> movieDataList;
    List<ImageData> imageDataList;
    MovieRecViewAdapter movieRecViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movieDataList = new ArrayList<>();
        imageDataList = new ArrayList<>();

        imagesGrid = findViewById(R.id.imagesListRecView);

        imagesGrid.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        imagesGrid.setLayoutManager(gridLayoutManager);

        movieRecViewAdapter = new MovieRecViewAdapter(getApplicationContext(), imageDataList, this, isNetworkOn());

        imagesGrid.setAdapter(movieRecViewAdapter);

        if (isNetworkOn()) {
            new RunAsyncConnection().execute();
        } else {
            getFavoriteMovies();
        }

    }

    private boolean isNetworkOn() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
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
                if(!getSortOrderFromPreferences().equals("Favorites")) {
                    jsonString = NetworkUtilities.downloadJSON(NetworkUtilities.buildURL(getSortOrderFromPreferences()));

                    movieDataList.clear();
                    imageDataList.clear();
                    movieDataList = JSONUtilities.allMoviesData(jsonString);
                    if (movieDataList.size() > 0) {
                        for (int i = 0; i < movieDataList.size(); i++) {
                            URL stringURL = NetworkUtilities.buildImageURL(movieDataList.get(i).getMoviePoster());
                            ImageData imageData = new ImageData(stringURL.toString(), movieDataList.get(i).getMovieID(), movieID(), null);
                            imageDataList.add(imageData);
                        }
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

            movieRecViewAdapter = new MovieRecViewAdapter(getApplicationContext(), imageDataList, new OnClickRecView() {
                @Override
                public void onRecyclerViewClick(int position) {
                    String title = movieDataList.get(position).getTitleMovie();
                    Log.d("MOVIE TITLE", title);

                    Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);

                    intent.putExtra(MOVIE_DETAIL, movieDataList.get(position));
                    startActivity(intent);
                }
            }, isNetworkOn());
            imagesGrid.setAdapter(movieRecViewAdapter);
        }
    }

    private void getFavoriteMovies() {
        try {
            Cursor cursor = getContentResolver().query(FavouritesContentProvider.BASE_CONTENT_URI,
                    null,
                    null,
                    null,
                    FavouritesContract.FavouritesEntry.MOVIE_ID);
            assert cursor != null;
            while (cursor.moveToNext()) {
                ImageData imageData = new ImageData(null,
                        cursor.getString(cursor.getColumnIndex(FavouritesContract.FavouritesEntry.MOVIE_ID)),
                        movieID(),
                        cursor.getString(cursor.getColumnIndex(FavouritesContract.FavouritesEntry.MOVIE_POSTER)));
                imageDataList.add(imageData);

                MovieData movieData = new MovieData(cursor.getString(cursor.getColumnIndex(FavouritesContract.FavouritesEntry.MOVIE_TITLE)),
                        cursor.getString(cursor.getColumnIndex(FavouritesContract.FavouritesEntry.MOVIE_DATE)),
                        cursor.getString(cursor.getColumnIndex(FavouritesContract.FavouritesEntry.MOVIE_POSTER)),
                        cursor.getString(cursor.getColumnIndex(FavouritesContract.FavouritesEntry.MOVIE_RATING)),
                        cursor.getString(cursor.getColumnIndex(FavouritesContract.FavouritesEntry.MOVIE_PLOT)),
                        cursor.getString(cursor.getColumnIndex(FavouritesContract.FavouritesEntry.MOVIE_ID)));
                movieDataList.add(movieData);
            }
            cursor.close();
        } finally {
            movieRecViewAdapter = new MovieRecViewAdapter(this, imageDataList, new OnClickRecView() {
                @Override
                public void onRecyclerViewClick(int position) {
                    String title = imageDataList.get(position).getImageName();
                    Log.d("MOVIE TITLE", title + " going offline");

                    Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);

                    intent.putExtra(MOVIE_DETAIL, movieDataList.get(position));
                    startActivity(intent);
                }
            }, isNetworkOn());
        }
    }

    private List<String> movieID() {
        List<String> movieID = new ArrayList<>();
        Cursor c = getContentResolver().query(FavouritesContentProvider.BASE_CONTENT_URI,
                null,
                null,
                null,
                null);

        if (c == null) {
            return null;
        } else {
            try {
                while (c.moveToNext()) {
                    movieID.add(c.getString(c.getColumnIndex(FavouritesContract.FavouritesEntry.MOVIE_ID)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return movieID;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(isNetworkOn()) {
            new RunAsyncConnection().execute();
        } else {
            getFavoriteMovies();
        }
    }
}