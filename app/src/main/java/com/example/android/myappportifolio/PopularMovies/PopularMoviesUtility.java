package com.example.android.myappportifolio.PopularMovies;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.android.myappportifolio.PopularMovies.PopularMoviesData.PopularMoviesContract;
import com.example.android.myappportifolio.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Luis on 11/6/2015.
 */
public class PopularMoviesUtility {

    static Context context;

    //Just a suport class
    private static class TaskParameter {
        public Context ctx;
        public Bitmap[] images;
        public String tag;
        public String[][] moviesInfo;

        TaskParameter(Context context, Bitmap[] images, String[][] moviesInfo, String tag) {
            ctx = context;
            this.images = images;
            this.tag = tag;
            this.moviesInfo = moviesInfo;
        }
    }

    //Save the posters of the movies on memory by another thread
    public static void saveImages(Context ctx, String[][] movieInfo, Bitmap[] images, String tag) {
        Save save = new Save();
        TaskParameter tp = new TaskParameter(ctx, images, movieInfo, tag);
        save.execute(tp);
    }

    private static class Save extends AsyncTask<TaskParameter, Void, Void> {

        @Override
        protected Void doInBackground(TaskParameter... params) {
            Context context = params[0].ctx;
            Bitmap[] images = params[0].images;
            String tag = params[0].tag;
            String[][] moviesInfo = params[0].moviesInfo;

            ContentResolver resolver = context.getContentResolver();
            Uri resultUri = null;
            ContentValues values = new ContentValues();

            long id = 0;
            if (tag.equals("Popular")) {
                for (int i = 0; i < images.length; i++) {
                    values.put(PopularMoviesContract.PopularEntry.COLUMN_POSTER, convertImageToBytes(images[i]));
                    id = id + resolver.update(PopularMoviesContract.PopularEntry.CONTENT_URI, values, PopularMoviesContract.PopularEntry.COLUMN_ID + " = " + moviesInfo[i][5], null);
                    values.clear();
                }
            } else if (tag.equals("Rate")) {
                for (int i = 0; i < images.length; i++) {

                    values.put(PopularMoviesContract.RateEntry.COLUMN_POSTER, convertImageToBytes(images[i]));
                    id = id + resolver.update(PopularMoviesContract.RateEntry.CONTENT_URI, values, PopularMoviesContract.RateEntry.COLUMN_ID + " = " + moviesInfo[i][5], null);
                    values.clear();
                }
            }

            Log.v("Saved Images", "Save " + id + "images");
            return null;
        }
    }

    //Load the posters of the movies
    public static Bitmap[] loadImages(Context context, String tag) {
        ContentResolver resolver = context.getContentResolver();
        Bitmap[] images = new Bitmap[20];
        int i = 0;
        if (tag.equals("Popular")) {
            int columnIndex;
            Cursor cursor = resolver.query(PopularMoviesContract.PopularEntry.CONTENT_URI,
                    new String[]{PopularMoviesContract.PopularEntry.COLUMN_POSTER},
                    null,
                    null,
                    null);
            if (cursor.moveToFirst()) {
                columnIndex = cursor.getColumnIndex(PopularMoviesContract.PopularEntry.COLUMN_POSTER);
                do {
                    if (i < 20) {
                        images[i] = convertBytesToImage(cursor.getBlob(columnIndex));
                        i++;
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        if (tag.equals("Rate")) {
            int columnIndex;
            Cursor cursor = resolver.query(PopularMoviesContract.RateEntry.CONTENT_URI,
                    new String[]{PopularMoviesContract.RateEntry.COLUMN_POSTER},
                    null,
                    null,
                    null);
            if (cursor.moveToFirst()) {
                columnIndex = cursor.getColumnIndex(PopularMoviesContract.RateEntry.COLUMN_POSTER);
                do {
                    images[i] = convertBytesToImage(cursor.getBlob(columnIndex));
                    i++;
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return images;
    }

    //Load the info of the movies
    public static String[][] loadInfo(Context context, String tag) {
        ContentResolver resolver = context.getContentResolver();
        String[][] moviesInfo = new String[20][6];
        int i = 0;

        if (tag.equals("Popular")) {
            Cursor cursor = resolver.query(PopularMoviesContract.PopularEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
            if (cursor.moveToFirst()) {
                ArrayList<Integer> columnsIndex = new ArrayList<>();
                columnsIndex.add(cursor.getColumnIndex(PopularMoviesContract.PopularEntry.COLUMN_TITLE));
                columnsIndex.add(cursor.getColumnIndex(PopularMoviesContract.PopularEntry.COLUMN_POSTER_PATH));
                columnsIndex.add(cursor.getColumnIndex(PopularMoviesContract.PopularEntry.COLUMN_OVERVIEW));
                columnsIndex.add(cursor.getColumnIndex(PopularMoviesContract.PopularEntry.COLUMN_VOTE_AVERAGE));
                columnsIndex.add(cursor.getColumnIndex(PopularMoviesContract.PopularEntry.COLUMN_RELEASE_DATE));
                columnsIndex.add(cursor.getColumnIndex(PopularMoviesContract.PopularEntry.COLUMN_ID));
                do {
                    if (i < 20) {
                        moviesInfo[i][0] = cursor.getString(columnsIndex.get(0));
                        moviesInfo[i][1] = cursor.getString(columnsIndex.get(1));
                        moviesInfo[i][2] = cursor.getString(columnsIndex.get(2));
                        moviesInfo[i][3] = cursor.getString(columnsIndex.get(3));
                        moviesInfo[i][4] = cursor.getString(columnsIndex.get(4));
                        moviesInfo[i][5] = cursor.getString(columnsIndex.get(5));
                        i++;
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }
        } else if (tag.equals("Rate")) {
            Cursor cursor = resolver.query(PopularMoviesContract.RateEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
            if (cursor.moveToFirst()) {
                ArrayList<Integer> columnsIndex = new ArrayList<>();
                columnsIndex.add(cursor.getColumnIndex(PopularMoviesContract.RateEntry.COLUMN_TITLE));
                columnsIndex.add(cursor.getColumnIndex(PopularMoviesContract.RateEntry.COLUMN_POSTER_PATH));
                columnsIndex.add(cursor.getColumnIndex(PopularMoviesContract.RateEntry.COLUMN_OVERVIEW));
                columnsIndex.add(cursor.getColumnIndex(PopularMoviesContract.RateEntry.COLUMN_VOTE_AVERAGE));
                columnsIndex.add(cursor.getColumnIndex(PopularMoviesContract.RateEntry.COLUMN_RELEASE_DATE));
                columnsIndex.add(cursor.getColumnIndex(PopularMoviesContract.RateEntry.COLUMN_ID));
                do {
                    moviesInfo[i][0] = cursor.getString(columnsIndex.get(0));
                    moviesInfo[i][1] = cursor.getString(columnsIndex.get(1));
                    moviesInfo[i][2] = cursor.getString(columnsIndex.get(2));
                    moviesInfo[i][3] = cursor.getString(columnsIndex.get(3));
                    moviesInfo[i][4] = cursor.getString(columnsIndex.get(4));
                    moviesInfo[i][5] = cursor.getString(columnsIndex.get(5));
                    i++;
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return moviesInfo;
    }

    //Save the infos from the movies
    public static void saveInfo(Context context, String[][] movies, String tag) {
        ContentResolver resolver = context.getContentResolver();
        Uri resultUri = null;
        ContentValues values = new ContentValues();

        if (tag.equals("Popular")) {
            Uri url = PopularMoviesContract.PopularEntry.CONTENT_URI;
            for (int i = 0; i < movies.length; i++) {
                values.put(PopularMoviesContract.PopularEntry.COLUMN_TITLE, movies[i][0]);
                values.put(PopularMoviesContract.PopularEntry.COLUMN_POSTER_PATH, movies[i][1]);
                values.put(PopularMoviesContract.PopularEntry.COLUMN_OVERVIEW, movies[i][2]);
                values.put(PopularMoviesContract.PopularEntry.COLUMN_VOTE_AVERAGE, movies[i][3]);
                values.put(PopularMoviesContract.PopularEntry.COLUMN_RELEASE_DATE, movies[i][4]);
                values.put(PopularMoviesContract.PopularEntry.COLUMN_ID, movies[i][5]);
                resultUri = resolver.insert(url, values);
                values.clear();
            }
        } else if (tag.equals("Rate")) {
            Uri url = PopularMoviesContract.RateEntry.CONTENT_URI;
            for (int i = 0; i < movies.length; i++) {
                values.put(PopularMoviesContract.RateEntry.COLUMN_TITLE, movies[i][0]);
                values.put(PopularMoviesContract.RateEntry.COLUMN_POSTER_PATH, movies[i][1]);
                values.put(PopularMoviesContract.RateEntry.COLUMN_OVERVIEW, movies[i][2]);
                values.put(PopularMoviesContract.RateEntry.COLUMN_VOTE_AVERAGE, movies[i][3]);
                values.put(PopularMoviesContract.RateEntry.COLUMN_RELEASE_DATE, movies[i][4]);
                values.put(PopularMoviesContract.RateEntry.COLUMN_ID, movies[i][5]);
                resultUri = resolver.insert(url, values);
                values.clear();
            }
        }

        Log.v("Save Info Status: ", resultUri.toString());
    }

    //Save a movie info when it is favorited
    public static void saveFavoriteInfo(Context context, ArrayList<String> movieDetail) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        Cursor cursor = resolver.query(PopularMoviesContract.FavoriteEntry.CONTENT_URI, new String[]{PopularMoviesContract.FavoriteEntry.COLUMN_ID},
                null,
                null,
                null);

        boolean alreadyExist = false;
        int columIndex = cursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_ID);

        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(columIndex).equals(movieDetail.get(5))) {
                    alreadyExist = true;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        if (!alreadyExist) {
            values.put(PopularMoviesContract.FavoriteEntry.COLUMN_TITLE, movieDetail.get(0));
            values.put(PopularMoviesContract.FavoriteEntry.COLUMN_POSTER_PATH, movieDetail.get(1));
            values.put(PopularMoviesContract.FavoriteEntry.COLUMN_OVERVIEW, movieDetail.get(2));
            values.put(PopularMoviesContract.FavoriteEntry.COLUMN_VOTE_AVERAGE, movieDetail.get(3));
            values.put(PopularMoviesContract.FavoriteEntry.COLUMN_RELEASE_DATE, movieDetail.get(4));
            values.put(PopularMoviesContract.FavoriteEntry.COLUMN_ID, movieDetail.get(5));

            Uri resultUri = resolver.insert(PopularMoviesContract.FavoriteEntry.CONTENT_URI, values);

            Log.v("Save Fav Info Status: ", resultUri.toString());
        } else {
            Log.v("Save Fav Info Status: ", "Already favorited");
        }
    }

    //Save a movie poster when it is favorited
    public static void saveFavoriteImage(Context context, Bitmap image, String movieId) {

        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();

        values.put(PopularMoviesContract.FavoriteEntry.COLUMN_POSTER, convertImageToBytes(image));
        long id = resolver.update(PopularMoviesContract.FavoriteEntry.CONTENT_URI, values, PopularMoviesContract.FavoriteEntry.COLUMN_ID + " = " + movieId, null);


        Log.v("Saved Images", "Save " + id + " images");
    }

    //Load favorites' info
    public static String[][] loadFavoriteInfo(Context context) {
        ContentResolver resolver = context.getContentResolver();
        ArrayList<String> moviesInfo = new ArrayList<>();
        Cursor cursor = resolver.query(PopularMoviesContract.FavoriteEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        if (cursor.moveToFirst()) {
            ArrayList<Integer> columnsIndex = new ArrayList<>();
            columnsIndex.add(cursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_TITLE));
            columnsIndex.add(cursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_POSTER_PATH));
            columnsIndex.add(cursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_OVERVIEW));
            columnsIndex.add(cursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_VOTE_AVERAGE));
            columnsIndex.add(cursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_RELEASE_DATE));
            columnsIndex.add(cursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_ID));
            do {
                moviesInfo.add(cursor.getString(columnsIndex.get(0)));
                moviesInfo.add(cursor.getString(columnsIndex.get(1)));
                moviesInfo.add(cursor.getString(columnsIndex.get(2)));
                moviesInfo.add(cursor.getString(columnsIndex.get(3)));
                moviesInfo.add(cursor.getString(columnsIndex.get(4)));
                moviesInfo.add(cursor.getString(columnsIndex.get(5)));
            } while (cursor.moveToNext());
        }
        cursor.close();

        String[][] movieInfo = new String[moviesInfo.size() / 6][6];
        for (int i = 0; i < movieInfo.length; i++) {
            for (int j = 0; j < movieInfo[0].length; j++) {
                movieInfo[i][j] = moviesInfo.get((i * 6) + j);
            }
        }
        return movieInfo;
    }

    //Load one favorites' image
    public static Bitmap[] loadFavoriteImage(Context context) {
        ContentResolver resolver = context.getContentResolver();
        ArrayList<Bitmap> bitImages = new ArrayList<>();
        int columnIndex;
        Cursor cursor = resolver.query(PopularMoviesContract.FavoriteEntry.CONTENT_URI,
                new String[]{PopularMoviesContract.FavoriteEntry.COLUMN_POSTER},
                null,
                null,
                null);
        if (cursor.moveToFirst()) {
            columnIndex = cursor.getColumnIndex(PopularMoviesContract.FavoriteEntry.COLUMN_POSTER);
            do {
                bitImages.add(convertBytesToImage(cursor.getBlob(columnIndex)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        Bitmap[] images = new Bitmap[bitImages.size()];
        for (int i = 0; i < images.length; i++) {
            images[i] = bitImages.get(i);
        }
        return images;
    }

    //Erase a favorite
    public static void removeFavorite(Context context, String movieId) {
        ContentResolver resolver = context.getContentResolver();
        resolver.delete(PopularMoviesContract.FavoriteEntry.CONTENT_URI, PopularMoviesContract.FavoriteEntry.COLUMN_ID + " = " + movieId, null);
    }

    //Download new images on another thread and open save method to save then
    public static void DownloadImage(String[][] moviesInfo, Context context, String tag) {
        TaskParameter taskParameter = new TaskParameter(context, null, moviesInfo, tag);
        GetImage getImage = new GetImage();
        getImage.execute(taskParameter);
    }

    private static class GetImage extends AsyncTask<TaskParameter, Void, Bitmap[]> {

        String tag = "";
        Context context;
        String[][] info;

        @Override
        protected Bitmap[] doInBackground(TaskParameter... params) {
            Bitmap[] images = new Bitmap[20];
            info = params[0].moviesInfo;
            context = params[0].ctx;
            tag = params[0].tag;
            for (int i = 0; i < info.length; i++) {
                if (!info[i][1].equals("null")) {
                    try {
                        images[i] = Picasso.with(context).load(info[i][1])
                                .error(R.drawable.ic_cancel_black_24dp)
                                .placeholder(R.drawable.ic_image_black_24dp)
                                .get();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    images[i] = null;
                }
            }
            return images;
        }

        @Override
        protected void onPostExecute(Bitmap[] images) {
            super.onPostExecute(images);
            PopularMoviesUtility.saveImages(context, info, images, tag);
        }
    }

    //Delete old info when info was updated
    public static void DeleteOldInfo(Context context, ArrayList<String> oldIds, String preference) {
        ContentResolver resolver = context.getContentResolver();
        Uri contentUri = null;
        String idColumn = null;
        if (preference.equals("Popular")) {
            contentUri = PopularMoviesContract.PopularEntry.CONTENT_URI;
            idColumn = PopularMoviesContract.PopularEntry.COLUMN_ID;
        } else if (preference.equals("Rate")) {
            contentUri = PopularMoviesContract.RateEntry.CONTENT_URI;
            idColumn = PopularMoviesContract.RateEntry.COLUMN_ID;
        }
        for (int i = 0; i < oldIds.size(); i++) {
            resolver.delete(contentUri, idColumn + " = " + oldIds.get(i), null);
        }
    }

    public static String getPreference(Context context, Activity activity) {
        SharedPreferences sharedPref = activity.getPreferences(context.MODE_PRIVATE);
        String defaultValue = context.getResources().getString(R.string.pref_movies_default);
        return sharedPref.getString(context.getString(R.string.pref_movies_key), defaultValue);
    }

    //Convert Dp to pixel to fit the images
    public static int convertDipToPixels(int dip, Context context) {
        int padding_in_dp = dip;  // 6 dps
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (padding_in_dp * scale + 0.5f);
    }

    // convert from bitmap to byte array
    public static byte[] convertImageToBytes(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
            return stream.toByteArray();
        } else {
            return null;
        }
    }

    // convert from byte array to bitmap
    public static Bitmap convertBytesToImage(byte[] image) {
        if (image != null) {
            return BitmapFactory.decodeByteArray(image, 0, image.length);
        } else {
            return null;
        }
    }


        // Download de data from themoviedb API
        public static class downloadMovieData extends AsyncTask<Context, Void, Boolean> {

            private final String API_KEY = "5efacb99e47c9cb39f08ce2dc7138c15";
            private final String LOG_TAG = downloadMovieData.class.getSimpleName();
            private final String BASE_URL = "api.themoviedb.org";
            private final String FIRST_PARAMETER = "3";
            private final String DISCOVER_PARAMETER = "discover";
            private final String MOVIE_PARAMETER = "movie";
            private final String SORT_PARAMETER_IDENTIFIER = "sort_by";
            private final String APY_KEY_IDENTIFIER = "api_key";

            String searchType;
            String[][] movies = new String[20][6];
            String preference;
            Context context;


            @Override
            protected Boolean doInBackground(Context... params) {

                context = params[0];
                Boolean hasInternet = new Boolean("true");

                preference = "Popular";

                //Check preferences to fetch the right data
                if (preference.equals("Rate")) {
                    searchType = "vote_average.desc";
                } else {
                    searchType = "popularity.desc";
                }

                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                String finalIDBMJsonString = null;
                String movieListJsonString = null;

                //Buold URI
                try {

                    Uri.Builder builder = new Uri.Builder();
                    builder.scheme("https")
                            .authority(BASE_URL)
                            .appendPath(FIRST_PARAMETER)
                            .appendPath(DISCOVER_PARAMETER)
                            .appendPath(MOVIE_PARAMETER)
                            .appendQueryParameter(SORT_PARAMETER_IDENTIFIER, searchType)
                            .appendQueryParameter(APY_KEY_IDENTIFIER, API_KEY);
                    ;

                    String myUrl = builder.build().toString();
                    URL movieList = new URL(myUrl);

                    Log.v(LOG_TAG, "Built URL: " + myUrl);

                    //Create the connection
                    urlConnection = (HttpURLConnection) movieList.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();


                    //Read the input Stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        //Nothing was get
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        //Reading the Json file and jump a line just for debuging purposes
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        return null;
                    }

                    movieListJsonString = buffer.toString();
                    //Get the info from Json file
                    try {
                        movies = getInfoFromJson(movieListJsonString);
                    } catch (JSONException j) {
                        Log.e(LOG_TAG, "Coudld not fetch data from Json File");
                    }

                    Log.v(LOG_TAG, "Movie List Json: " + movieListJsonString);

                    //Build the path to movie poster
                    builder = new Uri.Builder();
                    builder.scheme("http")
                            .authority("image.tmdb.org")
                            .appendPath("t")
                            .appendPath("p")
                            .appendPath("w185");

                    myUrl = builder.build().toString();

                    //Add the fetched information on an array
                    for (int i = 0; i < movies.length; i++) {
                        if (!movies[i][1].equals("null")) {
                            movies[i][1] = myUrl + movies[i][1];
                        }
                    }
                    Log.v(LOG_TAG, movies[0][1]);
                } catch (IOException e) {
                    Log.v(LOG_TAG, "Error on getting artist Id Json", e);
                    hasInternet = new Boolean("false");
                } finally {
                    //Close connection and BufferedReader
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e(LOG_TAG, "Error closing Stream", e);
                        }
                    }
                }
                return hasInternet;

            }

            //Set the adapter to the GridView
            @Override
            protected void onPostExecute(Boolean hasInternet) {
                super.onPostExecute(hasInternet);
                Bitmap[] images = PopularMoviesUtility.loadImages(context, preference);
                boolean hasNet = hasInternet.booleanValue();
                boolean hasNewInfo = false;
                boolean imageNull = false;
                ArrayList<String> oldInfo = new ArrayList<>();
                String[][] movieInfo = PopularMoviesUtility.loadInfo(context, preference);
                for (int i = 0; i < movies.length; i++) {
                    if (hasNet && (!movies[i][5].equals(movieInfo[i][5]))) {
                        hasNewInfo = true;
                    }
                }
                for (int i = 0; i < images.length; i++) {
                    if (hasNet && (images == null)) {
                        hasNewInfo = true;
                    }
                }
                if (hasNewInfo) {
                    //Check here what the diferences between the new info fetched and the info saved and update only the diferences without needing to download more images than the necessary
                    if (images != null && movieInfo != null) {
                        boolean checker = false;
                        for (int i = 0; i < movies.length; i++) {
                            checker = false;
                            for (int j = i; j < movieInfo.length; j++) {
                                if (movies[i][5].equals(movieInfo[j][5])) {
                                    Bitmap holder = images[j];
                                    Bitmap holder2 = images[i];
                                    images[j] = holder2;
                                    images[i] = holder;
                                    checker = true;
                                }
                            }
                            if (checker == false) {
                                images[i] = null;

                            }
                        }

                        //Get the info that is not usefull anymore to erase from the device
                        for (int i = 0; i < movieInfo.length; i++) {
                            for (int j = 0; j < movies.length; j++) {
                                checker = false;
                                if (movies[j][5].equals(movieInfo[i][5])) {
                                    checker = true;
                                }
                            }
                            if (checker == false) {
                                oldInfo.add(movieInfo[i][5]);
                            }
                        }
                    }
                    PopularMoviesUtility.DownloadImage(movies, context, preference);
                    PopularMoviesUtility.DeleteOldInfo(context, oldInfo, preference);
                }

                if (movies != null && hasNewInfo) {

                    for (int i = 0; i < movies.length; i++) {
                    }
                    PopularMoviesUtility.saveInfo(context, movies, preference);

                } else if (movies == null) {
                    Toast.makeText(context, "Couldn't connect, please check if you have internet connection", Toast.LENGTH_LONG).show();
                }
            }
        }

        //Get data from Json
        private static String[][] getInfoFromJson (String moviesInfoJson)throws JSONException {

            String[][] movies = new String[20][6];

            JSONObject moviesJsonFile = new JSONObject(moviesInfoJson);

            JSONArray moviesJsonArray = moviesJsonFile.getJSONArray("results");

            for (int i = 0; i < 20; i++) {
                movies[i][0] = moviesJsonArray.getJSONObject(i).getString("original_title");
                movies[i][1] = moviesJsonArray.getJSONObject(i).getString("poster_path");
                movies[i][2] = moviesJsonArray.getJSONObject(i).getString("overview");
                movies[i][3] = moviesJsonArray.getJSONObject(i).getString("vote_average");
                movies[i][4] = moviesJsonArray.getJSONObject(i).getString("release_date");
                movies[i][5] = moviesJsonArray.getJSONObject(i).getString("id");
            }
            return movies;
        }
}
