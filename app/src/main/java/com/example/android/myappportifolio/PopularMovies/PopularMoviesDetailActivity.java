package com.example.android.myappportifolio.PopularMovies;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ShareActionProvider;

import com.example.android.myappportifolio.R;

import java.util.ArrayList;

public class PopularMoviesDetailActivity extends Activity {

    static ArrayList<String> reviews;
    private static ShareActionProvider mShareActionProvider;
    private static Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_movies_detail);

        //Only create another fragment to past information if in Two Pane mode because I using Scroll View on Detail Fragment and two children will cause a error
        if (savedInstanceState == null && PopularMoviesMain.mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putStringArrayList(PopularMoviesDetailActivityFragment.MOVIES_LIST_NAME, getIntent().getStringArrayListExtra(PopularMoviesDetailActivityFragment.MOVIES_LIST_NAME));
            arguments.putString("preference", getIntent().getStringExtra("preference"));
            arguments.putByteArray("byteArray", getIntent().getByteArrayExtra("byteArray"));

            PopularMoviesDetailActivityFragment fragment = new PopularMoviesDetailActivityFragment();
            fragment.setArguments(arguments);

            getFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_popular_movies_detail, menu);

        MenuItem item = menu.findItem(R.id.detail_item_share);

        mShareActionProvider = (ShareActionProvider) item.getActionProvider();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.detail_item_share) {
            if (intent != null) {
                startActivity(intent);
            }
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

    public static void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
        intent = shareIntent;
    }
}
