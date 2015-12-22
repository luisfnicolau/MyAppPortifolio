package com.example.android.myappportifolio.PopularMovies.Widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.myappportifolio.PopularMovies.PopularMoviesData.PopularMoviesContract;
import com.example.android.myappportifolio.PopularMovies.PopularMoviesDetailActivityFragment;
import com.example.android.myappportifolio.PopularMovies.PopularMoviesUtility;
import com.example.android.myappportifolio.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Luis on 12/21/2015.
 */
public class DetailWidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Bitmap[] posters = new Bitmap[20];
            private String[][] movieInfo = new String[20][6];
            boolean running = false;
            private String preference = "Popular";

            private Cursor data = null;
            @Override
            public void onCreate() {
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
//                Thread thread = new Thread() {
//                    public void run() {
//                        posters = PopularMoviesUtility.getImages(getApplicationContext());
//                        movieInfo = PopularMoviesUtility.loadInfo(getApplicationContext(), preference);
//                    }
//                };
//                thread.start();
//                try {
//                    thread.join();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(PopularMoviesContract.PopularEntry.CONTENT_URI,
                        new String[]{PopularMoviesContract.PopularEntry.COLUMN_POSTER},
                        null,
                        null,
                        null);
                if (data.moveToFirst()) {
                    int columnIndex = data.getColumnIndex(PopularMoviesContract.PopularEntry.COLUMN_POSTER);
                    int i = 0;
                    do {
                        if (i < 20) {
                            posters[i] = PopularMoviesUtility.convertBytesToImage(data.getBlob(columnIndex));
                            i++;
                        }
                    } while (data.moveToNext());
                }
                data.close();
                data = getContentResolver().query(PopularMoviesContract.PopularEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
                if (data.moveToFirst()) {
                    ArrayList<Integer> columnsIndex = new ArrayList<>();
                    columnsIndex.add(data.getColumnIndex(PopularMoviesContract.PopularEntry.COLUMN_TITLE));
                    columnsIndex.add(data.getColumnIndex(PopularMoviesContract.PopularEntry.COLUMN_POSTER_PATH));
                    columnsIndex.add(data.getColumnIndex(PopularMoviesContract.PopularEntry.COLUMN_OVERVIEW));
                    columnsIndex.add(data.getColumnIndex(PopularMoviesContract.PopularEntry.COLUMN_VOTE_AVERAGE));
                    columnsIndex.add(data.getColumnIndex(PopularMoviesContract.PopularEntry.COLUMN_RELEASE_DATE));
                    columnsIndex.add(data.getColumnIndex(PopularMoviesContract.PopularEntry.COLUMN_ID));
                    int i = 0;
                    do {
                        if (i < 20) {
                            movieInfo[i][0] = data.getString(columnsIndex.get(0));
                            movieInfo[i][1] = data.getString(columnsIndex.get(1));
                            movieInfo[i][2] = data.getString(columnsIndex.get(2));
                            movieInfo[i][3] = data.getString(columnsIndex.get(3));
                            movieInfo[i][4] = data.getString(columnsIndex.get(4));
                            movieInfo[i][5] = data.getString(columnsIndex.get(5));
                            i++;
                        }
                    } while (data.moveToNext());                }
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {


                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    System.out.println("td null");
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_detail_grid_item);
                if (posters[position] == null) {
                    try {
                        posters[position] = Picasso.with(getApplicationContext())
                                .load(movieInfo[position][1])
                                .error(R.drawable.ic_cancel_black_24dp)
                                .get();
                    } catch (IOException | RuntimeException e) {
                        e.printStackTrace();
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    setRemoteContentDescription(views, "etc");
                }

                if (posters[position] != null) {
                    views.setImageViewBitmap(R.id.widget_grid_item, posters[position]);
                } else {
                    views.setImageViewResource(R.id.widget_grid_item, R.drawable.ic_local_movies);
                }
                final Intent fillInIntent = new Intent();
//                String locationSetting =
//                        Utility.getPreferredLocation(DetailWidgetRemoteViewsService.this);
//                Uri weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
//                        locationSetting,
//                        dateInMillis);
//                fillInIntent.setData(PopularMoviesContract.PopularEntry.CONTENT_URI);
                ArrayList<String> moviesInfo = new ArrayList<>();
                for (int i = 0; i < movieInfo[0].length; i++) {
                    moviesInfo.add(movieInfo[position][i]);
                }
                byte[] byteArray = PopularMoviesUtility.convertImageToBytes(posters[position]);
                fillInIntent.putStringArrayListExtra(PopularMoviesDetailActivityFragment.MOVIES_LIST_NAME, moviesInfo);
                fillInIntent.putExtra(PopularMoviesDetailActivityFragment.PREFERENCE_NAME, preference);
                fillInIntent.putExtra(PopularMoviesDetailActivityFragment.BYTE_ARRAY_NAME, byteArray);
                views.setOnClickFillInIntent(R.id.widget_grid_item, fillInIntent);
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_detail_grid_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String description) {
        views.setContentDescription(R.id.widget_icon, description);
    }
}
