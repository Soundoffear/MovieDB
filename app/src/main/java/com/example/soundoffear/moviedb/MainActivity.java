package com.example.soundoffear.moviedb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.example.soundoffear.moviedb.model.MovieDataSaR;
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
    ArrayList<MovieDataSaR> movieDataSaRList = new ArrayList<>();

    private static final String PARCELABLE_KEY_STATE = "state_key";
    private static final String SCROLL_STATE = "scroll_state";
    private String lastSortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("onCREATE", " ---------------- onCreate --------------");
        setContentView(R.layout.activity_main);
        movieDataList = new ArrayList<>();
        imageDataList = new ArrayList<>();

        imagesGrid = findViewById(R.id.imagesListRecView);

        imagesGrid.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        imagesGrid.setLayoutManager(gridLayoutManager);

        movieRecViewAdapter = new MovieRecViewAdapter(getApplicationContext(), imageDataList, this, isNetworkOn(), getSortOrderFromPreferences());

        imagesGrid.setAdapter(movieRecViewAdapter);

        if (savedInstanceState != null) {
            Parcelable restoredScroll = savedInstanceState.getParcelable(SCROLL_STATE);
            imagesGrid.getLayoutManager().onRestoreInstanceState(restoredScroll);
            movieRestoration(savedInstanceState);

        } else {
            if (isNetworkOn()) {
                if (getSortOrderFromPreferences().equals("Favorites")) {
                    getFavoriteMovies();
                } else {
                    new RunAsyncConnection().execute();
                }
                Log.d("NETWORK", "ON");
            } else {
                Log.d("NETWORK", "OFF");
            }
        }

    }

    private void movieRestoration(Bundle savedInstanceState) {
        movieDataSaRList = savedInstanceState.getParcelableArrayList(PARCELABLE_KEY_STATE);
        assert movieDataSaRList != null;
        for (int i = 0; i < movieDataSaRList.size(); i++) {
            imageDataList.add(new ImageData(movieDataSaRList.get(i).getImageURL(),
                    movieDataSaRList.get(i).getImageID(),
                    movieDataSaRList.get(i).getMovieID(),
                    movieDataSaRList.get(i).getImageName()));
            movieDataList.add(new MovieData(movieDataSaRList.get(i).getTitleMovie(),
                    movieDataSaRList.get(i).getReleaseDate(),
                    movieDataSaRList.get(i).getMoviePoster(),
                    movieDataSaRList.get(i).getAvgVote(),
                    movieDataSaRList.get(i).getMoviePlot(),
                    movieDataSaRList.get(i).getImageID()));
        }
        movieRecViewAdapter = new MovieRecViewAdapter(this, imageDataList, new OnClickRecView() {
            @Override
            public void onRecyclerViewClick(int position) {
                Intent intentToStartDetailActivity = new Intent(MainActivity.this, MovieDetailActivity.class);
                intentToStartDetailActivity.putExtra(MOVIE_DETAIL, movieDataList.get(position));
                startActivity(intentToStartDetailActivity);
            }
        }, isNetworkOn(), getSortOrderFromPreferences());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            Parcelable restoredScroll = savedInstanceState.getParcelable(SCROLL_STATE);
            imagesGrid.getLayoutManager().onRestoreInstanceState(restoredScroll);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        lastSortOrder = getSortOrderFromPreferences();
        outState.putParcelable(SCROLL_STATE, imagesGrid.getLayoutManager().onSaveInstanceState());
        outState.putParcelableArrayList(PARCELABLE_KEY_STATE, movieDataSaRList);
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
                jsonString = NetworkUtilities.downloadJSON(NetworkUtilities.buildURL(getSortOrderFromPreferences()));
                movieDataList.clear();
                imageDataList.clear();
                List<MovieData> fromJSONdata = JSONUtilities.allMoviesData(jsonString);
                if (fromJSONdata.size() > 0) {
                    for (int i = 0; i < fromJSONdata.size(); i++) {
                        URL stringURL = NetworkUtilities.buildImageURL(fromJSONdata.get(i).getMoviePoster());
                        ImageData imageData = new ImageData(stringURL.toString(), JSONUtilities.allMoviesData(jsonString).get(i).getMovieID(), movieID(), null);
                        movieDataList.add(JSONUtilities.allMoviesData(jsonString).get(i));
                        imageDataList.add(imageData);
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
            movieDataSaRList.clear();
            for (int i = 0; i < movieDataList.size(); i++) {
                Log.d("MOVIE", movieDataList.get(i).getTitleMovie());
            }

            movieRecViewAdapter = new MovieRecViewAdapter(getApplicationContext(), imageDataList, new OnClickRecView() {
                @Override
                public void onRecyclerViewClick(int position) {
                    Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);

                    intent.putExtra(MOVIE_DETAIL, movieDataList.get(position));
                    startActivity(intent);
                }
            }, isNetworkOn(),
                    getSortOrderFromPreferences());
            for (int i = 0; i < imageDataList.size(); i++) {
                String movieURL = imageDataList.get(i).getImageURL();
                String movieID = imageDataList.get(i).getImageID();
                String movieName = imageDataList.get(i).getImageName();
                String movieTitle = movieDataList.get(i).getTitleMovie();
                String movieDate = movieDataList.get(i).getReleaseDate();
                String moviePoster = movieDataList.get(i).getMoviePoster();
                String movieAvgVote = movieDataList.get(i).getAvgVote();
                String moviePlot = movieDataList.get(i).getMoviePlot();
                movieDataSaRList.add(new MovieDataSaR(movieURL,
                        movieID,
                        movieName,
                        movieID(),
                        movieTitle,
                        movieDate,
                        moviePoster,
                        movieAvgVote,
                        moviePlot));
            }
            imagesGrid.setAdapter(movieRecViewAdapter);
        }
    }

    private void getFavoriteMovies() {
        imageDataList.clear();
        movieDataList.clear();
        movieDataSaRList.clear();
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
                for (int i = 0; i < imageDataList.size(); i++) {
                    String movieURL = imageDataList.get(i).getImageURL();
                    String movieID = imageDataList.get(i).getImageID();
                    String movieName = imageDataList.get(i).getImageName();
                    String movieTitle = movieDataList.get(i).getTitleMovie();
                    String movieDate = movieDataList.get(i).getReleaseDate();
                    String moviePoster = movieDataList.get(i).getMoviePoster();
                    String movieAvgVote = movieDataList.get(i).getAvgVote();
                    String moviePlot = movieDataList.get(i).getMoviePlot();
                    movieDataSaRList.add(new MovieDataSaR(movieURL,
                            movieID,
                            movieName,
                            movieID(),
                            movieTitle,
                            movieDate,
                            moviePoster,
                            movieAvgVote,
                            moviePlot));
                }
            }
            cursor.close();
        } finally {
            movieRecViewAdapter = new MovieRecViewAdapter(this, imageDataList, new OnClickRecView() {
                @Override
                public void onRecyclerViewClick(int position) {

                    Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);

                    intent.putExtra(MOVIE_DETAIL, movieDataList.get(position));
                    startActivity(intent);
                }
            }, isNetworkOn(),
                    getSortOrderFromPreferences());
            imagesGrid.setAdapter(movieRecViewAdapter);
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
        if (!lastSortOrder.equals(getSortOrderFromPreferences())) {
            if (isNetworkOn()) {
                if(getSortOrderFromPreferences().equals("Favorites")) {
                    getFavoriteMovies();
                } else {
                    new RunAsyncConnection().execute();
                }
            } else {
                getFavoriteMovies();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Bundle stateBundle = new Bundle();
        stateBundle.putParcelableArrayList(PARCELABLE_KEY_STATE, movieDataSaRList);
        stateBundle.putParcelable(SCROLL_STATE, imagesGrid.getLayoutManager().onSaveInstanceState());
        getIntent().putExtras(stateBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle restoredBundle;
        restoredBundle = getIntent().getExtras();
        if (restoredBundle != null) {
            imagesGrid.getLayoutManager().onRestoreInstanceState(restoredBundle.getParcelable(SCROLL_STATE));
        }
    }
}