package com.example.soundoffear.moviedb.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.soundoffear.moviedb.model.MovieData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by soundoffear on 01/03/2018.
 */

public class Favourites extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "favourites.db";

    Favourites(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE " + FavouritesContract.FavouritesEntry.TABLE_NAME + " ("
                + FavouritesContract.FavouritesEntry._ID + " INTEGER PRIMARY KEY, "
                + FavouritesContract.FavouritesEntry.MOVIE_TITLE + " TEXT NOT NULL, "
                + FavouritesContract.FavouritesEntry.MOVIE_DATE + " TEXT NOT NULL, "
                + FavouritesContract.FavouritesEntry.MOVIE_RATING + " TEXT NOT NULL, "
                + FavouritesContract.FavouritesEntry.MOVIE_PLOT + " TEXT NOT NULL, "
                + FavouritesContract.FavouritesEntry.MOVIE_ID + " TEXT NOT NULL, "
                + FavouritesContract.FavouritesEntry.MOVIE_POSTER + " TEXT NOT NULL)";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavouritesContract.FavouritesEntry.TABLE_NAME);
        onCreate(db);
    }

    public void insertMovie(MovieData movieData) {

        SQLiteDatabase movieDB = getWritableDatabase();

        ContentValues movieValues = new ContentValues();
        movieValues.put(FavouritesContract.FavouritesEntry.MOVIE_TITLE, movieData.getTitleMovie());
        movieValues.put(FavouritesContract.FavouritesEntry.MOVIE_DATE, movieData.getReleaseDate());
        movieValues.put(FavouritesContract.FavouritesEntry.MOVIE_RATING, movieData.getAvgVote());
        movieValues.put(FavouritesContract.FavouritesEntry.MOVIE_PLOT, movieData.getMoviePlot());

        movieDB.insert(FavouritesContract.FavouritesEntry.TABLE_NAME, null, movieValues);
        movieDB.close();

    }

    public List<MovieData> getAllFavMovies() {
        SQLiteDatabase movieFavDB = getReadableDatabase();

        List<MovieData> movieDataList = new ArrayList<>();

        Cursor cursor = movieFavDB.query(FavouritesContract.FavouritesEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        if(cursor.moveToFirst()) {
            do {
                String movieTitle = cursor.getColumnName(cursor.getColumnIndex(FavouritesContract.FavouritesEntry.MOVIE_TITLE));
                String movieDate = cursor.getColumnName(cursor.getColumnIndex(FavouritesContract.FavouritesEntry.MOVIE_DATE));
                String movieRating = cursor.getColumnName(cursor.getColumnIndex(FavouritesContract.FavouritesEntry.MOVIE_RATING));
                String moviePlot = cursor.getColumnName(cursor.getColumnIndex(FavouritesContract.FavouritesEntry.MOVIE_PLOT));
                String movieID = cursor.getColumnName(cursor.getColumnIndex(FavouritesContract.FavouritesEntry.MOVIE_ID));
                String moviePoster = cursor.getColumnName(cursor.getColumnIndex(FavouritesContract.FavouritesEntry.MOVIE_POSTER));
                MovieData movieData = new MovieData(movieTitle, movieDate, movieRating, moviePlot, movieID, moviePoster);
                movieDataList.add(movieData);
            } while (cursor.moveToNext());
        }
        cursor.close();
        movieFavDB.close();

        return movieDataList;

    }

    public void deleteMovie(String movieTitle) {
        SQLiteDatabase movieFavDB = getWritableDatabase();
        movieFavDB.delete(FavouritesContract.FavouritesEntry.MOVIE_TITLE, FavouritesContract.FavouritesEntry.TABLE_NAME + " = " + "'" + movieTitle + "'", null);
        movieFavDB.close();
    }

}
