package com.example.android.myappportifolio.PopularMovies;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.android.myappportifolio.R;

import java.util.ArrayList;

public class PopularMoviesDetailActivity extends Activity {

    static ArrayList<String> reviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_movies_detail);
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet) {
            PopularMoviesDetailActivityFragment fragment = new PopularMoviesDetailActivityFragment();

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_popular_movies_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Receive the reviews list from fragment
    public static void setReviews(ArrayList<String> rev) {
        reviews = rev;
    }

    //Launche the reviews list if a review is clicked
    public void launchReviews(View view) {
        Intent intent = new Intent(getApplicationContext(), PopularMoviesReviewsListPage.class);
        intent.putExtra("reviewsList", reviews);
        startActivity(intent);
    }
}
