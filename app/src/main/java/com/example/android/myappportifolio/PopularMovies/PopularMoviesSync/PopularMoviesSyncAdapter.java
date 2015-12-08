package com.example.android.myappportifolio.PopularMovies.PopularMoviesSync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by Luis on 04/12/2015.
 */
public class PopularMoviesSyncAdapter extends AbstractThreadedSyncAdapter{
    public PopularMoviesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Toast.makeText(getContext(), "SyncAdapter", Toast.LENGTH_SHORT).show();
    }

}
