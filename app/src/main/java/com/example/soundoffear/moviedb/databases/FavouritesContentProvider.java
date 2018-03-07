package com.example.soundoffear.moviedb.databases;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

public class FavouritesContentProvider extends ContentProvider {

    private static final String AUTHORITY = "com.example.soundoffear.moviedb.databases.FavouritesContentProvider";
    private static final String URL = "content://" + AUTHORITY + "/favourites";
    public static final Uri BASE_CONTENT_URI = Uri.parse(URL);

    public static final int FAVORITES = 1;
    public static final int FAVORITES_ID = 2;

    private static final UriMatcher fUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(AUTHORITY, "favourites", FAVORITES);
        matcher.addURI(AUTHORITY, "favourites/#", FAVORITES_ID);

        return matcher;
    }

    private Favourites favourites;

    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {

        favourites = new Favourites(getContext());

        db = favourites.getWritableDatabase();

        return db != null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Cursor movieCursor;

        switch (fUriMatcher.match(uri)) {
            case FAVORITES: {
                movieCursor = db.query(FavouritesContract.FavouritesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case FAVORITES_ID: {
                movieCursor = db.query(FavouritesContract.FavouritesEntry.TABLE_NAME,
                        projection,
                        FavouritesContract.FavouritesEntry._ID + " = ? ",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                break;
            }
            default:
                return null;
        }

        movieCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return movieCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        /**switch (fUriMatcher.match(uri)) {
            case FAVORITES:
                return "vnd.android.cursor.dir/vnd.example.movies";
            case FAVORITES_ID:
                return "vnd.android.cursor.item/vnd.example.movies";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }*/
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri _uri;
        long rowID = db.insert(FavouritesContract.FavouritesEntry.TABLE_NAME, null, values);

        if (rowID > 0) {
            _uri = ContentUris.withAppendedId(BASE_CONTENT_URI, rowID);

        } else {
            throw new SQLException("Failed to insert row into " + rowID);
        }

        getContext().getContentResolver().notifyChange(_uri, null);
        return _uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        int count = 0;

        switch (fUriMatcher.match(uri)) {
            case FAVORITES:
                //nothing - no bulk delete
                break;
            case FAVORITES_ID:
                String id = uri.getLastPathSegment();
                count = db.delete(FavouritesContract.FavouritesEntry.TABLE_NAME,
                        FavouritesContract.FavouritesEntry.MOVIE_ID + " = " + id + (!TextUtils.isEmpty(selection) ?  " AND (" + selection + ')' : ""), selectionArgs);
                break;
                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
