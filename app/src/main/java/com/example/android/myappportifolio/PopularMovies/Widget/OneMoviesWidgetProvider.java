package com.example.android.myappportifolio.PopularMovies.Widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

/**
 * Created by Luis on 12/21/2015.
 */
public class OneMoviesWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Intent intent = new Intent(context, OneMoviesWidgetIntentService.class);
        context.startService(intent);

//        appWidgetIds = intent.getIntArrayExtra("AppWidgetsId");
//        Bitmap[] moviesPoster = PopularMoviesUtility.loadImages(context, "Popular");
//        int movieIconId = R.drawable.ic_local_movies;
//        String description = "This Nuts";
//        int i = 0;
//
//        for (int appWidgetId : appWidgetIds) {
//            int layoutId = R.layout.widget_small;
//            RemoteViews views = new RemoteViews(context.getPackageName(), layoutId);
//
//            Intent intent = new Intent(context, MoviesWidgetIntentService.class);
//            views.setRemoteAdapter(R.id.widget_grid_view, intent);
//            views.setEmptyView(R.id.widget_grid_view, R.id.widget_empty_view);
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
//                setRemoteContentDescription(views, description);
//            }
//
//            Intent launchIntent = new Intent(context, PopularMoviesMain.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, 0);
//            views.setOnClickPendingIntent(R.id.widget, pendingIntent);
//
//            appWidgetManager.updateAppWidget(appWidgetId, views);
//            i++;
//        }
    }

//    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
//    private void setRemoteContentDescription(RemoteViews views, String description) {
//        views.setContentDescription(R.id.widget_icon, description);
//    }


    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
            context.startService(new Intent(context, OneMoviesWidgetIntentService.class));
    }
}
