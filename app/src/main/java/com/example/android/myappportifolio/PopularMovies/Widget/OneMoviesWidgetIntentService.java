package com.example.android.myappportifolio.PopularMovies.Widget;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.widget.RemoteViews;

import com.example.android.myappportifolio.PopularMovies.PopularMoviesMain;
import com.example.android.myappportifolio.PopularMovies.PopularMoviesUtility;
import com.example.android.myappportifolio.R;

/**
 * Created by Luis on 12/21/2015.
 */
public class OneMoviesWidgetIntentService extends IntentService {



    public OneMoviesWidgetIntentService() {
        super("MovieWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, OneMoviesWidgetProvider.class));
        Context context = getApplicationContext();


        Bitmap[] moviesPoster = PopularMoviesUtility.loadImages(context, "Popular");
        String[][] moviesInfo = PopularMoviesUtility.loadInfo(context, "Popular");
        int movieIconId = R.drawable.ic_local_movies;
        String description = moviesInfo[0][0];
        int i = 0;

        for (int appWidgetId : appWidgetIds) {
            int layoutId = R.layout.widget_small;
            RemoteViews views = new RemoteViews(getPackageName(), layoutId);

            // Add the data to the RemoteViews
            views.setImageViewBitmap(R.id.widget_icon, moviesPoster[0]);
            // Content Descriptions for RemoteViews were only added in ICS MR1
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                setRemoteContentDescription(views, description);
            }

            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(this, PopularMoviesMain.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private static void setRemoteContentDescription(RemoteViews views, String description) {
        views.setContentDescription(R.id.widget_icon, description);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PopularMoviesUtility.downloadMovieData dmd = new PopularMoviesUtility.downloadMovieData();
        dmd.execute(getApplicationContext());
    }
}
