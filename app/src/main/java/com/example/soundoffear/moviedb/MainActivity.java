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
import android.support.v7.widget.LinearLayoutManager;
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
import com.example.soundoffear.moviedb.utilities.MovieDataSaR;
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

        if(savedInstanceState != null) {
            Parcelable restoredScroll = savedInstanceState.getParcelable(SCROLL_STATE);
            imagesGrid.getLayoutManager().onRestoreInstanceState(restoredScroll);
            Log.d("STATE SAVED", "--------- STATE ------ SAVED ------");
            restartRecyclerView(savedInstanceState);

        } else {
            if (isNetworkOn()) {
                new RunAsyncConnection().execute();
                Log.d("NETWORK", "ON");
            } else {

                getFavoriteMovies();
                Log.d("NETWORK", "OFF");
            }
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("onRestore", "------- onRestoreInstanceState ------");
        restartRecyclerView(savedInstanceState);
        if(savedInstanceState != null) {
            Parcelable restoredScroll = savedInstanceState.getParcelable(SCROLL_STATE);
            imagesGrid.getLayoutManager().onRestoreInstanceState(restoredScroll);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        lastSortOrder = getSortOrderFromPreferences();
        Log.d("MOVIE SAR saving", String.valueOf(movieDataSaRList.size()));
        outState.putParcelableArrayList(PARCELABLE_KEY_STATE, movieDataSaRList);
        outState.putParcelable(SCROLL_STATE, imagesGrid.getLayoutManager().onSaveInstanceState());
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
                if(getSortOrderFromPreferences().equals("Favorites")) {
                    getFavoriteMovies();
                }
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
                movieDataSaRList.add(new MovieDataSaR(movieURL, movieID, movieName, movieID()));
            }
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

                    Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);

                    intent.putExtra(MOVIE_DETAIL, movieDataList.get(position));
                    startActivity(intent);
                }
            }, isNetworkOn(),
                    getSortOrderFromPreferences());
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
        Log.d("RESTART", "------- onRestart ------");
        if (!lastSortOrder.equals(getSortOrderFromPreferences())) {
            if (isNetworkOn()) {
                new RunAsyncConnection().execute();
            } else {
                getFavoriteMovies();
            }
        } else {
            restartRecyclerView(getIntent().getExtras());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Bundle savedData = new Bundle();
        savedData.putParcelableArrayList(PARCELABLE_KEY_STATE, movieDataSaRList);
        savedData.putParcelable(SCROLL_STATE, imagesGrid.getLayoutManager().onSaveInstanceState());
        getIntent().putExtras(savedData);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("RESUME", "------- onResume ------");
        Bundle restoredData = getIntent().getExtras();
        if (restoredData != null) {
            imagesGrid.getLayoutManager().onRestoreInstanceState(restoredData.getParcelable(SCROLL_STATE));
            restartRecyclerView(restoredData);
        }
    }

    private void restartRecyclerView(Bundle restoredData) {
        movieDataSaRList.clear();
        movieDataSaRList = restoredData.getParcelableArrayList(PARCELABLE_KEY_STATE);
        Log.d("MOVIE SAR restoring 1", String.valueOf(movieDataSaRList.size()));
        if (movieDataSaRList != null) {
            Log.d("Restoring", "------- ======== RESTORING ======= --------");
            for (int i = 0; i < movieDataSaRList.size(); i++) {
                ImageData imageData = new ImageData(movieDataSaRList.get(i).getImageURL(),
                        movieDataSaRList.get(i).getImageID(),
                        movieID(),
                        movieDataSaRList.get(i).getImageName());
                Log.d("TEST IMAGE DATA", imageData.getImageURL() + " _ " + imageData.getImageID());
                imageDataList.add(imageData);
            }
            movieRecViewAdapter = new MovieRecViewAdapter(this,
                    imageDataList,
                    new OnClickRecView() {
                        @Override
                        public void onRecyclerViewClick(int position) {
                            Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);

                            intent.putExtra(MOVIE_DETAIL, movieDataList.get(position));
                            startActivity(intent);
                        }
                    },
                    isNetworkOn(),
                    getSortOrderFromPreferences());
            imagesGrid.setAdapter(movieRecViewAdapter);
        }

        Log.d("MOVIE SAR restoring 2", String.valueOf(movieDataSaRList.size()));
    }
}