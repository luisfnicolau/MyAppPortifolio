package com.example.android.myappportifolio.PopularMovies;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.android.myappportifolio.R;

import java.util.ArrayList;

public class PopularMoviesMain extends AppCompatActivity implements PopularMoviesMainFragment.Callback {

    public static boolean mTwoPane;
    private final String DETAILFRAGMENT_TAG = "DFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_movies_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new PopularMoviesDetailActivityFragment())
                        .commit();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_popular_movies_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        SharedPreferences sharedPref = getPreferences(getApplicationContext().MODE_PRIVATE);
        String defaultValue = getResources().getString(R.string.pref_movies_default);
        String preference = sharedPref.getString(getString(R.string.pref_movies_key), defaultValue);

        if (id == R.id.action_most_popular) {
//            pref.setSummary("Popular");
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.pref_movies_key), "Popular");
            editor.commit();
            Intent intent = new Intent(getApplicationContext(), PopularMoviesMain.class);
            startActivity(intent);
        }
        if (id == R.id.action_high_rate) {
//            pref.setSummary("Rate");
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.pref_movies_key), "Rate");
            editor.commit();
            Intent intent = new Intent(getApplicationContext(), PopularMoviesMain.class);
            startActivity(intent);
        }
        if (id == R.id.action_favorite) {
//            pref.setSummary("Fav");
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.pref_movies_key), "Fav");
            editor.commit();
            Intent intent = new Intent(getApplicationContext(), PopularMoviesMain.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(ArrayList<String> moviesInfo, String preference, byte[] byteArray, View view) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putStringArrayList(PopularMoviesDetailActivityFragment.MOVIES_LIST_NAME, moviesInfo);
            args.putString(PopularMoviesDetailActivityFragment.PREFERENCE_NAME, preference);
            args.putByteArray(PopularMoviesDetailActivityFragment.BYTE_ARRAY_NAME, byteArray);

            PopularMoviesDetailActivityFragment fragment = new PopularMoviesDetailActivityFragment();
            fragment.setArguments(args);

            getFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment,DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, PopularMoviesDetailActivity.class);
            intent.putStringArrayListExtra(PopularMoviesDetailActivityFragment.MOVIES_LIST_NAME, moviesInfo);
            intent.putExtra(PopularMoviesDetailActivityFragment.PREFERENCE_NAME, preference);
            intent.putExtra(PopularMoviesDetailActivityFragment.BYTE_ARRAY_NAME, byteArray);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptionsCompat options = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(this, view, getString(R.string.transition_start));
                ActivityCompat.startActivity(this, intent, options.toBundle());
            } else {
                startActivity(intent);
            }
        }
    }
}
