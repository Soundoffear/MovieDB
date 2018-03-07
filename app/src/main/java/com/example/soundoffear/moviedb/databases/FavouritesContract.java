package com.example.soundoffear.moviedb.databases;

import android.net.Uri;
import android.provider.BaseColumns;

import static com.example.soundoffear.moviedb.databases.FavouritesContentProvider.BASE_CONTENT_URI;

/**
 * Created by soundoffear on 01/03/2018.
 */

public class FavouritesContract {

    public static class FavouritesEntry implements BaseColumns {

        public static final String TABLE_NAME = "favourites_movies_table";
        public static final String MOVIE_TITLE = "movie_title";
        public static final String MOVIE_DATE = "movie_date";
        public static final String MOVIE_PLOT = "movie_plot";
        public static final String MOVIE_RATING = "movie_rating";
        public static final String MOVIE_ID = "movie_id";
        public static final String MOVIE_POSTER = "movie_poster";


        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

    }
}
