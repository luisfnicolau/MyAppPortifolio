package com.example.android.myappportifolio.PopularMovies.PopularMoviesData;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Luis on 11/25/2015.
 */
public class PopularMoviesDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "movies.db";

    public PopularMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_POPULAR_TABLE = "CREATE TABLE " + PopularMoviesContract.PopularEntry.TABLE_NAME  + " (" +
                PopularMoviesContract.PopularEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PopularMoviesContract.PopularEntry.COLUMN_TITLE + " TEXT," +
                PopularMoviesContract.PopularEntry.COLUMN_POSTER_PATH + " TEXT, " +
                PopularMoviesContract.PopularEntry.COLUMN_OVERVIEW + " TEXT, " +
                PopularMoviesContract.PopularEntry.COLUMN_VOTE_AVERAGE + " TEXT, " +
                PopularMoviesContract.PopularEntry.COLUMN_RELEASE_DATE + " TEXT," +
                PopularMoviesContract.PopularEntry.COLUMN_ID + " TEXT UNIQUE NOT NULL," +
                PopularMoviesContract.PopularEntry.COLUMN_POSTER + " TEXT" +
                " );";

        final String SQL_CREATE_RATE_TABLE = "CREATE TABLE " + PopularMoviesContract.RateEntry.TABLE_NAME  + " (" +
                PopularMoviesContract.RateEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PopularMoviesContract.RateEntry.COLUMN_TITLE + " TEXT," +
                PopularMoviesContract.RateEntry.COLUMN_POSTER_PATH + " TEXT, " +
                PopularMoviesContract.RateEntry.COLUMN_OVERVIEW + " TEXT, " +
                PopularMoviesContract.RateEntry.COLUMN_VOTE_AVERAGE + " TEXT, " +
                PopularMoviesContract.RateEntry.COLUMN_RELEASE_DATE + " TEXT," +
                PopularMoviesContract.RateEntry.COLUMN_ID + " TEXT UNIQUE NOT NULL," +
                PopularMoviesContract.RateEntry.COLUMN_POSTER + " TEXT" +
                " );";

        final String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE " + PopularMoviesContract.FavoriteEntry.TABLE_NAME  + " (" +
                PopularMoviesContract.FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PopularMoviesContract.FavoriteEntry.COLUMN_TITLE + " TEXT," +
                PopularMoviesContract.FavoriteEntry.COLUMN_POSTER_PATH + " TEXT, " +
                PopularMoviesContract.FavoriteEntry.COLUMN_OVERVIEW + " TEXT, " +
                PopularMoviesContract.FavoriteEntry.COLUMN_VOTE_AVERAGE + " TEXT, " +
                PopularMoviesContract.FavoriteEntry.COLUMN_RELEASE_DATE + " TEXT," +
                PopularMoviesContract.FavoriteEntry.COLUMN_ID + " TEXT UNIQUE NOT NULL," +
                PopularMoviesContract.FavoriteEntry.COLUMN_POSTER + " TEXT" +
                " );";

        final String SQL_CREATE_TRAILERS_TABLE = "CREATE TABLE " + PopularMoviesContract.TrailersAndReviewsEntry.TABLE_NAME  + " (" +
                PopularMoviesContract.TrailersAndReviewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PopularMoviesContract.TrailersAndReviewsEntry.COLUMN_TRAILERS + " TEXT," +
                PopularMoviesContract.TrailersAndReviewsEntry.COLUMN_LOC_KEY + " TEXT," +
                PopularMoviesContract.TrailersAndReviewsEntry.COLUMN_REVIEWS + " TEXT " +
                ");";



        sqLiteDatabase.execSQL(SQL_CREATE_POPULAR_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_RATE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PopularMoviesContract.PopularEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PopularMoviesContract.RateEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PopularMoviesContract.FavoriteEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PopularMoviesContract.TrailersAndReviewsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
