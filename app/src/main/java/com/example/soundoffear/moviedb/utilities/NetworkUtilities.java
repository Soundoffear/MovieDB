package com.example.soundoffear.moviedb.utilities;

import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NetworkUtilities {

    private static final String MOVIE_DB_URL_POPULAR = "https://api.themoviedb.org/3/movie/popular?api_key=";
    private static final String MOVIE_DB_URL_TOP = "https://api.themoviedb.org/3/movie/top_rated?api_key=";
    private static final String IMAGE_DB_URL = "https://image.tmdb.org/t/p/";

    private static final String API_KEY = "xxxxxxxxxxxxxx";

    //private static final String IMAGE_SIZE = "w185";
    private static final String IMAGE_SIZE_500 = "w500";


    public static URL buildURL(String order) {
        Uri builder;

        if (order.equals("Popularity")) {
            builder = Uri.parse(MOVIE_DB_URL_POPULAR)
                    .buildUpon()
                    .appendQueryParameter("api_key", API_KEY)
                    .build();
        } else {
            builder = Uri.parse(MOVIE_DB_URL_TOP)
                    .buildUpon()
                    .appendQueryParameter("api_key", API_KEY)
                    .build();
        }

        URL url = null;
        try {
            url = new URL(builder.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildImageURL(String imageStringName) {
        Uri uri = Uri.parse(IMAGE_DB_URL)
                .buildUpon()
                .appendPath(IMAGE_SIZE_500)
                .appendEncodedPath(imageStringName)
                .build();

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildImageBigURL(String imageStringName) {
        Uri uri = Uri.parse(IMAGE_DB_URL)
                .buildUpon()
                .appendPath(IMAGE_SIZE_500)
                .appendEncodedPath(imageStringName)
                .build();

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String downloadJSON(URL url) throws IOException {
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        String result = null;

        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setReadTimeout(3000);

            httpURLConnection.setConnectTimeout(3000);

            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code " + responseCode);
            }

            inputStream = httpURLConnection.getInputStream();
            if (inputStream != null) {
                result = readStream(inputStream);
            }

        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return result;
    }

    private static final String TRAILER_URL = "https://api.themoviedb.org/3/movie/";
    private static final String VIDEOS = "videos";

    public static URL getYouTubeKey(String movieID) {

        Uri uri = Uri.parse(TRAILER_URL).buildUpon()
                .appendPath(movieID)
                .appendEncodedPath(VIDEOS)
                .appendQueryParameter("api_key", API_KEY).build();

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return url;

    }

    private static final String REVIEWS = "reviews";

    public static URL getReviews(String movieID) {
        Uri uri = Uri.parse(TRAILER_URL)
                .buildUpon()
                .appendPath(movieID)
                .appendEncodedPath(REVIEWS)
                .appendQueryParameter("api_key", API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String downloadTrailerJSON(URL url) throws IOException {
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        String result = null;

        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setReadTimeout(3000);

            httpURLConnection.setConnectTimeout(3000);

            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code " + responseCode);
            }

            inputStream = httpURLConnection.getInputStream();
            if (inputStream != null) {
                result = readStream(inputStream);
            }

        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return result;
    }

    private static String readStream(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder finalBuild = new StringBuilder();
        String sFinal;
        while ((sFinal = bufferedReader.readLine()) != null) {
            finalBuild.append(sFinal).append('\n');
        }
        return finalBuild.toString();
    }

}