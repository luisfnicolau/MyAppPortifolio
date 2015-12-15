package com.example.android.myappportifolio.PopularMovies.PopularMoviesData;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by Luis on 11/26/2015.
 */
public class PopularMoviesProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static final int POPULAR = 100;
    static final int RATE = 200;
    static final int FAV = 300;
    static final int REV = 400;

    private PopularMoviesDbHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new PopularMoviesDbHelper(getContext());
        return (mOpenHelper == null) ? false : true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case POPULAR:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        PopularMoviesContract.PopularEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
             break;
            }
            case RATE:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        PopularMoviesContract.RateEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case FAV:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        PopularMoviesContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case REV:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        PopularMoviesContract.TrailersAndReviewsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        return PopularMoviesContract.PopularEntry.CONTENT_ITEM_TYPE;
    }

    //Problem is here
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri = null;
        switch (match) {
            case POPULAR: {
                long _id = db.insert(PopularMoviesContract.PopularEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = PopularMoviesContract.PopularEntry.buildPopularUri(_id);
                else
                throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case RATE: {
                long _id = db.insert(PopularMoviesContract.RateEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = PopularMoviesContract.RateEntry.buildPopularUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case FAV: {
                long _id = db.insert(PopularMoviesContract.FavoriteEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = PopularMoviesContract.FavoriteEntry.buildPopularUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REV: {
                long _id = db.insert(PopularMoviesContract.TrailersAndReviewsEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = PopularMoviesContract.TrailersAndReviewsEntry.buildTrailerAdnReviewsUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        if (selection == null)
            selection = "1";

        switch (match){
            case POPULAR: {
                rowsDeleted = db.delete(PopularMoviesContract.PopularEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case RATE: {
                rowsDeleted = db.delete(PopularMoviesContract.RateEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case FAV: {
                rowsDeleted = db.delete(PopularMoviesContract.FavoriteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case REV: {
                rowsDeleted = db.delete(PopularMoviesContract.TrailersAndReviewsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
            }
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match){
            case POPULAR:{
                rowsUpdated = db.update(PopularMoviesContract.PopularEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case RATE:{
                rowsUpdated = db.update(PopularMoviesContract.RateEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case FAV:{
                rowsUpdated = db.update(PopularMoviesContract.FavoriteEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }


    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String autority = PopularMoviesContract.CONTENT_AUTHORITY;

        // 2) Use the addURI function to match each of the types.  Use the constants from
        // WeatherContract to help define the types to the UriMatcher.

        matcher.addURI(autority, PopularMoviesContract.PATH_MOST_POPULAR, POPULAR);
        matcher.addURI(autority, PopularMoviesContract.PATH_HIGH_RATE, RATE );
        matcher.addURI(autority, PopularMoviesContract.PATH_FAVORITE, FAV );
        matcher.addURI(autority, PopularMoviesContract.PATH_TRAILERSANDREVIEWS, REV );

        // 3) Return the new matcher!
        return matcher;
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
