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
import com.example.android.myappportifolio.PopularMovies.PopularMoviesData.PopularMoviesDbHelper;
import com.example.android.myappportifolio.R;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
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
//            for (int i = 0; i < images.length; i++) {
//                FileOutputStream outImage = null;
//            File fileImage = new File(context.getFilesDir(), tag + String.valueOf(i) + ".bmp");
//            fileImage.delete();
//            try {
//                outImage = new FileOutputStream(fileImage);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//            if (images[i] != null) {
//                images[i].compress(Bitmap.CompressFormat.PNG, 100, outImage);
//            }
//            try {
//                outImage.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
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
        }

//        Bitmap[] images = new Bitmap[20];
//        for (int i = 0; i < 20; i++) {
//            File fileImage = new File(context.getFilesDir(), tag + String.valueOf(i) + ".bmp");
//            try {
//                images[i] = BitmapFactory.decodeStream(new FileInputStream(fileImage));
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//        }
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

//        BufferedReader log = null;
//        String input;
//        String[][] movies = new String[20][6];
//        int j = 0;
//        String inputHolder = "";
//        for (int i = 0; i < 20; i++) {
//            try {
//                File file = new File(context.getFilesDir(), tag + String.valueOf(i) + ".txt");
//                log = new BufferedReader(new FileReader(file));
//                while ((input = log.readLine()) != null) {
//                    movies[i][j] = input;
//                    j++;
//                }
//                j = 0;
//            } catch (IOException e) {
//                System.out.println("Não há arquivos salvos\r\n");
//            } catch (ArrayIndexOutOfBoundsException e) {
//                System.out.println("Não há arquivos salvos\r\n");
//            } finally {
//                if (log != null) {
//                    try {
//                        log.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//        return movies;
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


//        for (int i = 0; i < 20; i++) {
//
//            BufferedWriter out = null;
//            try //Create a file to store information
//            {
//                File file = new File(context.getFilesDir(), tag + String.valueOf(i) + ".txt");
//                file.delete();
//                out = new BufferedWriter(new FileWriter(file));
//                for (int j = 0; j < movies[0].length; j++) {
//                    if (j == 2) {
//                        //Format the string so it will be saved without skipped lines that cause bugs
//                        movies[i][j] = movies[i][j].replaceAll("[\r\n]", "");
//                    }
//                    out.write(movies[i][j] + "\r\n");
//                }
//            } catch (IOException e) {
//                System.out.println("Failed to create a file");
//            } finally {
//                try {
//                    if (out != null) {
//                        out.close();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }

    //Save a movie info when it is favorited
    public static void saveFavoriteInfo(Context context, ArrayList<String> movieDetail) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();

        values.put(PopularMoviesContract.FavoriteEntry.COLUMN_TITLE, movieDetail.get(0));
        values.put(PopularMoviesContract.FavoriteEntry.COLUMN_POSTER_PATH, movieDetail.get(1));
        values.put(PopularMoviesContract.FavoriteEntry.COLUMN_OVERVIEW, movieDetail.get(2));
        values.put(PopularMoviesContract.FavoriteEntry.COLUMN_VOTE_AVERAGE, movieDetail.get(3));
        values.put(PopularMoviesContract.FavoriteEntry.COLUMN_RELEASE_DATE, movieDetail.get(4));
        values.put(PopularMoviesContract.FavoriteEntry.COLUMN_ID, movieDetail.get(5));
//        values.put(PopularMoviesContract.FavoriteEntry.COLUMN_POSTER, movieDetail.get(6));

        Uri resultUri = resolver.insert(PopularMoviesContract.FavoriteEntry.CONTENT_URI, values);

        Log.v("Save Favorite Info Status: ", resultUri.toString());

//        BufferedWriter out = null;
//        BufferedReader reader = null;
//        boolean alreadyExist = false;
//        String input = "";
//
//
//        File logFile = new File(context.getFilesDir(), "log");
//        try {
//            reader = new BufferedReader(new FileReader(logFile));
//        } catch (FileNotFoundException e) {
//            try {
//                out = new BufferedWriter(new FileWriter(logFile));
//            } catch (IOException e1) {
//                e1.printStackTrace();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            ArrayList<String> name = new ArrayList<>();
//            reader = new BufferedReader(new FileReader(logFile));
//            while ((input = reader.readLine()) != null) {
//                name.add(input);
//            }
//            for (int i = 0; i < name.size(); i++) {
//                if (name.get(i).equals(movieDetail.get(5))) {
//                    alreadyExist = true;
//                }
//            }
//            if (!alreadyExist) {
//                logFile.delete();
//                name.add(movieDetail.get(5));
//                out = new BufferedWriter(new FileWriter(logFile));
//                for (int i = 0; i < name.size(); i++) {
//                    out.write(name.get(i) + "\r\n");
//                }
//                out.close();
//                File infoFile = new File(context.getFilesDir(), "favorite" + movieDetail.get(5) + "info.txt");
//                out = new BufferedWriter(new FileWriter(infoFile));
//                for (int i = 0; i < movieDetail.size(); i++) {
//                    out.write(movieDetail.get(i) + "\r\n");
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (reader != null) {
//                    reader.close();
//                }
//                if (out != null) {
//                    out.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    //Save a movie poster when it is favorited
    public static void saveFavoriteImage(Context context, Bitmap image, String name) {
        FileOutputStream outImage = null;
        File fileImage = new File(context.getFilesDir(), name + ".bmp");
        try {
            outImage = new FileOutputStream(fileImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (image != null) {
            image.compress(Bitmap.CompressFormat.PNG, 100, outImage);
        }
        try {
            outImage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Load favorites' info
    public static String[][] loadFavoriteInfo(Context context) {
        BufferedReader reader = null;
        String input;
        String[][] movieInfo = new String[0][0];

        File logFile = new File(context.getFilesDir(), "log");
        try {
            ArrayList<String> names = new ArrayList<>();
            reader = new BufferedReader(new FileReader(logFile));
            while ((input = reader.readLine()) != null) {
                names.add(input);
            }
            reader.close();
            movieInfo = new String[names.size()][6];
            for (int i = 0; i < names.size(); i++) {
                File infoFile = new File(context.getFilesDir(), "favorite" + names.get(i) + "info.txt");
                reader = new BufferedReader(new FileReader(infoFile));
                int j = 0;
                while ((input = reader.readLine()) != null) {
                    movieInfo[i][j] = input;
                    j++;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return movieInfo;
    }

    //Load one favorites' image
    public static Bitmap[] loadFavoriteImage(Context context, String[] names) {
        Bitmap[] images = new Bitmap[names.length];
        for (int i = 0; i < names.length; i++) {
            File fileImage = new File(context.getFilesDir(), names[i] + ".bmp");
            try {
                images[i] = BitmapFactory.decodeStream(new FileInputStream(fileImage));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return images;
    }

    //Erase a favorite
    public static void removeFavorite(Context context, String s) {
        File infoFile = new File(context.getFilesDir(), "favorite" + s + "info.txt");
        File imageFile = new File(context.getFilesDir(), s + ".bmp");
        File log = new File(context.getFilesDir(), "log");
        String input = "";
        infoFile.delete();
        imageFile.delete();
        try {
            ArrayList<String> checkNames = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(log));
            while ((input = reader.readLine()) != null) {
                if (!input.equals(s)) {
                    checkNames.add(input);
                }
            }
            log.delete();
            reader.close();
            BufferedWriter writer = new BufferedWriter(new FileWriter(log));
            for (int i = 0; i < checkNames.size(); i++) {
                writer.write(checkNames.get(i) + "\r\n");
            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public static void DeleteOldInfo(Context context, ArrayList<String> oldIds) {
        for (int i = 0; i < oldIds.size(); i++) {
            File infoFile = new File(context.getFilesDir(), "favorite" + oldIds.get(i) + "info.txt");
            File imageFile = new File(context.getFilesDir(), oldIds.get(i) + ".bmp");
            File log = new File(context.getFilesDir(), "log");
            String input = "";
            infoFile.delete();
            imageFile.delete();
            try {
                ArrayList<String> checkNames = new ArrayList<>();
                BufferedReader reader = new BufferedReader(new FileReader(log));
                while ((input = reader.readLine()) != null) {
                    if (!input.equals(oldIds.get(i))) {
                        checkNames.add(input);
                    }
                }
                log.delete();
                reader.close();
                BufferedWriter writer = new BufferedWriter(new FileWriter(log));
                for (int j = 0; j < checkNames.size(); j++) {
                    writer.write(checkNames.get(j) + "\r\n");
                }
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    //Triyng to write info on database using Content Provider
    public static void putSomethingInDb(Context context, String[] moviesInfo, String databaseName) {
        context.deleteDatabase(PopularMoviesDbHelper.DATABASE_NAME);
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(PopularMoviesContract.PopularEntry.COLUMN_TITLE, moviesInfo[0]);
        values.put(PopularMoviesContract.PopularEntry.COLUMN_POSTER, moviesInfo[1]);
        values.put(PopularMoviesContract.PopularEntry.COLUMN_OVERVIEW, moviesInfo[2]);
        values.put(PopularMoviesContract.PopularEntry.COLUMN_VOTE_AVERAGE, moviesInfo[3]);
        values.put(PopularMoviesContract.PopularEntry.COLUMN_RELEASE_DATE, moviesInfo[4]);
        values.put(PopularMoviesContract.PopularEntry.COLUMN_ID, moviesInfo[5]);
        System.out.println(PopularMoviesContract.PopularEntry.CONTENT_URI);
        resolver.insert(PopularMoviesContract.PopularEntry.CONTENT_URI, values);

//        SQLiteDatabase db = new PopularMoviesDbHelper(context)
//                .getWritableDatabase();
//        System.out.println("Database criada: " + db.isOpen());
//
//        ContentValues values = new ContentValues();
//        if (databaseName.equals("Popular")) {
//            values.put(PopularMoviesContract.PopularEntry.COLUMN_TITLE, moviesInfo[0]);
//            values.put(PopularMoviesContract.PopularEntry.COLUMN_POSTER, moviesInfo[1]);
//            values.put(PopularMoviesContract.PopularEntry.COLUMN_OVERVIEW, moviesInfo[2]);
//            values.put(PopularMoviesContract.PopularEntry.COLUMN_VOTE_AVERAGE, moviesInfo[3]);
//            values.put(PopularMoviesContract.PopularEntry.COLUMN_RELEASE_DATE, moviesInfo[4]);
//            values.put(PopularMoviesContract.PopularEntry.COLUMN_ID, moviesInfo[5]);
//
//            System.out.println("Valores do ContentValues Popular: " + values);
//
//            long result = db.insert(PopularMoviesContract.PopularEntry.TABLE_NAME, null, values);
//            System.out.println("Resultado do insert: " + result);
//            db.close();
//
//        } else if (databaseName.equals("Rate")) {
//            values.put(PopularMoviesContract.RateEntry.COLUMN_TITLE, moviesInfo[0]);
//            values.put(PopularMoviesContract.RateEntry.COLUMN_POSTER, moviesInfo[1]);
//            values.put(PopularMoviesContract.RateEntry.COLUMN_OVERVIEW, moviesInfo[2]);
//            values.put(PopularMoviesContract.RateEntry.COLUMN_VOTE_AVERAGE, moviesInfo[3]);
//            values.put(PopularMoviesContract.RateEntry.COLUMN_RELEASE_DATE, moviesInfo[4]);
//            values.put(PopularMoviesContract.RateEntry.COLUMN_ID, moviesInfo[5]);
//
//            System.out.println("Valores do ContentValues Popular: " + values);
//
//            long result = db.insert(PopularMoviesContract.RateEntry.TABLE_NAME, null, values);
//            System.out.println("Resultado do insert: " + result);
//            db.close();
//
//        } else if (databaseName.equals("Fav")) {
//            values.put(PopularMoviesContract.FavoriteEntry.COLUMN_TITLE, moviesInfo[0]);
//            values.put(PopularMoviesContract.FavoriteEntry.COLUMN_POSTER, moviesInfo[1]);
//            values.put(PopularMoviesContract.FavoriteEntry.COLUMN_OVERVIEW, moviesInfo[2]);
//            values.put(PopularMoviesContract.FavoriteEntry.COLUMN_VOTE_AVERAGE, moviesInfo[3]);
//            values.put(PopularMoviesContract.FavoriteEntry.COLUMN_RELEASE_DATE, moviesInfo[4]);
//            values.put(PopularMoviesContract.FavoriteEntry.COLUMN_ID, moviesInfo[5]);
//
//            System.out.println("Valores do ContentValues Popular: " + values);
//
//            long result = db.insert(PopularMoviesContract.FavoriteEntry.TABLE_NAME, null, values);
//            System.out.println("Resultado do insert: " + result);
//            db.close();
//
//        }
    }

    //Just made a simple read from a db (yet to be implemented)
    public static void ReadSomethingFromDb(Context context, String preference) {
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(PopularMoviesContract.PopularEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
//        SQLiteDatabase db = new PopularMoviesDbHelper(context)
//                .getReadableDatabase();
//        String table = null;
//        System.out.println("Abriu Db pra ler " + db.isOpen());
//        if (preference.equals("Popular")) {
//            table = PopularMoviesContract.PopularEntry.TABLE_NAME;
//        } else if (preference.equals("Rate")) {
//            table = PopularMoviesContract.RateEntry.TABLE_NAME;
//        } else if (preference.equals("Fav")) {
//            table = PopularMoviesContract.FavoriteEntry.TABLE_NAME;
//        }
        ArrayList<Integer> list = new ArrayList<>();
//
//        Cursor cursor = db.query(table,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null);
        if (cursor.moveToFirst()) {
            list.add(cursor.getColumnIndex(PopularMoviesContract.PopularEntry.COLUMN_TITLE));
            list.add(cursor.getColumnIndex(PopularMoviesContract.PopularEntry.COLUMN_POSTER));
            list.add(cursor.getColumnIndex(PopularMoviesContract.PopularEntry.COLUMN_OVERVIEW));
            list.add(cursor.getColumnIndex(PopularMoviesContract.PopularEntry.COLUMN_VOTE_AVERAGE));
            list.add(cursor.getColumnIndex(PopularMoviesContract.PopularEntry.COLUMN_RELEASE_DATE));
            list.add(cursor.getColumnIndex(PopularMoviesContract.PopularEntry.COLUMN_ID));
        }
        if (cursor.moveToFirst()) {
            do {
                System.out.println(cursor.getString(list.get(0)));
//                System.out.println(cursor.getString(list.get(1)));
//                System.out.println(cursor.getString(list.get(2)));
//                System.out.println(cursor.getString(list.get(3)));
//                System.out.println(cursor.getString(list.get(4)));
//                System.out.println(cursor.getString(list.get(5)));

            } while (cursor.moveToNext());
        }
        cursor.close();
//        context.deleteDatabase(PopularMoviesDbHelper.DATABASE_NAME);
    }
}
