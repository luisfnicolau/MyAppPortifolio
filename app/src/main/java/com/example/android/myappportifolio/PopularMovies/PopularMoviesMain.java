package com.example.android.myappportifolio.PopularMovies;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.myappportifolio.R;

public class PopularMoviesMain extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_movies_main);
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


}
