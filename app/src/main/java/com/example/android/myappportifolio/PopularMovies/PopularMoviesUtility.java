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

import com.example.android.myappportifolio.PopularMovies.PopularMoviesData.PopularMoviesContract;
import com.example.android.myappportifolio.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
                    images[i] = convertBytesToImage(cursor.getBlob(columnIndex));
                    i++;
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
                    moviesInfo[i][0] = cursor.getString(columnsIndex.get(0));
                    moviesInfo[i][1] = cursor.getString(columnsIndex.get(1));
                    moviesInfo[i][2] = cursor.getString(columnsIndex.get(2));
                    moviesInfo[i][3] = cursor.getString(columnsIndex.get(3));
                    moviesInfo[i][4] = cursor.getString(columnsIndex.get(4));
                    moviesInfo[i][5] = cursor.getString(columnsIndex.get(5));
                    i++;
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
}
