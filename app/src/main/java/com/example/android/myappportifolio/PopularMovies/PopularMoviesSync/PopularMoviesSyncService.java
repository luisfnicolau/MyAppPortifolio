package com.example.android.myappportifolio.PopularMovies.PopularMoviesSync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Luis on 04/12/2015.
 */
public class PopularMoviesSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static PopularMoviesSyncAdapter sPopularMoviesSyncAdapter = null;


    @Override
    public void onCreate() {
        Log.d("PopularMovieSyncService", "onCreate - PopularMoviesSyncService");
        synchronized (sSyncAdapterLock) {
            if (sPopularMoviesSyncAdapter == null) {
                sPopularMoviesSyncAdapter = new PopularMoviesSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sPopularMoviesSyncAdapter.getSyncAdapterBinder();
    }
}
