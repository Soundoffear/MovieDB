package com.example.soundoffear.moviedb;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.soundoffear.moviedb.databases.FavouritesContentProvider;
import com.example.soundoffear.moviedb.databases.FavouritesContract;
import com.example.soundoffear.moviedb.fragments.PlotDetail;
import com.example.soundoffear.moviedb.fragments.Reviews;
import com.example.soundoffear.moviedb.fragments.TrailersDetail;
import com.example.soundoffear.moviedb.model.MovieData;
import com.example.soundoffear.moviedb.utilities.NetworkUtilities;
import com.squareup.picasso.Picasso;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MovieDetailActivity extends AppCompatActivity {

    TextView movie_title;
    TextView release_date;
    TextView average_vote;

    ImageView movie_poster;
    ImageButton fav_image_button;

    private String plot;
    public static String movieID;

    private boolean isFav = false;

    MovieData movieData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Intent intent = getIntent();

        movie_title = findViewById(R.id.detail_title_value);
        release_date = findViewById(R.id.detail_release_date_value);
        average_vote = findViewById(R.id.detail_vote_value);

        movie_poster = findViewById(R.id.detail_movie_poster);

        fav_image_button = findViewById(R.id.detail_fav_image_button);

        movieData = (MovieData) intent.getSerializableExtra(MainActivity.MOVIE_DETAIL);

        movie_title.setText(movieData.getTitleMovie());
        release_date.setText(movieData.getReleaseDate());
        average_vote.setText(movieData.getAvgVote());

        String posterImage = NetworkUtilities.buildImageBigURL(movieData.getMoviePoster()).toString();

        if(isNetworkOn()) {
            Picasso.with(getApplicationContext()).load(posterImage).into(movie_poster);
        } else {
            movie_poster.setImageBitmap(imageFromStorage(movieData.getMoviePoster()));
        }

        plot = movieData.getMoviePlot();
        movieID = movieData.getMovieID();

        TabLayout tabLayout = findViewById(R.id.detail_tabLayout);
        ViewPager viewPager = findViewById(R.id.detail_viewPager);

        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        checkFavorites();

        fav_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFav = !isFav;
                if (isFav) {
                    fav_image_button.setColorFilter(getResources().getColor(R.color.colorAccent));
                    insertData();
                } else {
                    fav_image_button.setColorFilter(getResources().getColor(R.color.colorWhite));
                    deleteData();
                }
            }
        });
    }

    private Bitmap imageFromStorage(String imageName) {
        Bitmap imageBitMap = null;
        try {
            FileInputStream fileInputStream = openFileInput(imageName);
            imageBitMap = BitmapFactory.decodeStream(fileInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imageBitMap;
    }

    private boolean isNetworkOn() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    private void saveToInternalStorage(String imageName) {

        movie_poster.buildDrawingCache();
        Bitmap posterBitMap = movie_poster.getDrawingCache();

        try {
            FileOutputStream fileOutputStream = openFileOutput(imageName, MODE_PRIVATE);
            posterBitMap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void deleteMoviePoster(String moviePoster) {
        this.deleteFile(moviePoster);
    }

    void insertData() {
        ContentValues movieCV = new ContentValues();
        movieCV.put(FavouritesContract.FavouritesEntry.MOVIE_TITLE, movieData.getTitleMovie());
        movieCV.put(FavouritesContract.FavouritesEntry.MOVIE_DATE, movieData.getReleaseDate());
        movieCV.put(FavouritesContract.FavouritesEntry.MOVIE_RATING, movieData.getAvgVote());
        movieCV.put(FavouritesContract.FavouritesEntry.MOVIE_PLOT, movieData.getMoviePlot());
        movieCV.put(FavouritesContract.FavouritesEntry.MOVIE_ID, movieData.getMovieID());
        movieCV.put(FavouritesContract.FavouritesEntry.MOVIE_POSTER, movieData.getMoviePoster().replace('/',' '));

        getContentResolver().insert(FavouritesContract.FavouritesEntry.CONTENT_URI, movieCV);
        saveToInternalStorage(movieData.getMoviePoster().replace('/',' '));
    }

    void deleteData() {
        Uri uri = Uri.parse(FavouritesContentProvider.BASE_CONTENT_URI + "/" + movieData.getMovieID());
        getContentResolver().delete(uri, null, null);
        deleteMoviePoster(movieData.getMoviePoster().replace('/',' '));
    }

    void checkFavorites() {
        Cursor cursor = getContentResolver().query(FavouritesContentProvider.BASE_CONTENT_URI,
                null,
                null,
                null,
                null);
        if (cursor == null) {
            return;

        } else {
            while (cursor.moveToNext()) {
                try {
                    Log.d("Title ",
                            cursor.getString(cursor.getColumnIndex(FavouritesContract.FavouritesEntry.MOVIE_TITLE)) + " "
                            + cursor.getString(cursor.getColumnIndex(FavouritesContract.FavouritesEntry.MOVIE_ID)));
                    String movieIDcursor = cursor.getString(cursor.getColumnIndex(FavouritesContract.FavouritesEntry.MOVIE_ID));
                    if (movieIDcursor.equals(movieID)) {
                        fav_image_button.setColorFilter(getResources().getColor(R.color.colorAccent));
                        movie_poster.setImageBitmap(imageFromStorage(movieData.getMoviePoster().replace('/',' ')));
                        Log.d("MOVIE IMG", movieData.getMoviePoster().replace('/', ' '));
                        isFav = true;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        cursor.close();
    }

    public static final String PLOT_DETAIL = "plot_from_activity";

    class ViewPagerAdapter extends FragmentPagerAdapter {


        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    PlotDetail plotDetail = new PlotDetail();
                    Bundle bundle = new Bundle();
                    bundle.putString(PLOT_DETAIL, plot);
                    plotDetail.setArguments(bundle);
                    return plotDetail;
                case 1:
                    return new TrailersDetail();
                case 2:
                    return new Reviews();
                default:
                    return null;

            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Plot";
                case 1:
                    return "Trailers";
                case 2:
                    return "Reviews";
            }
            return null;
        }
    }
}
