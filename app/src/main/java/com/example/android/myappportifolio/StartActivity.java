package com.example.android.myappportifolio;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.android.myappportifolio.PopularMovies.PopularMoviesMain;

public class StartActivity extends AppCompatActivity {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        context = getApplicationContext();
    }

    //Don't need a Settings Menu on this part
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_start, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    public void onClick(View v) {

        switch (v.getId()){
            case R.id.spotify_streamer_button:
                Intent intent = new Intent(context, PopularMoviesMain.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptionsCompat options = ActivityOptionsCompat
                            .makeSceneTransitionAnimation(this, v, getString(R.string.transition_start));
                    ActivityCompat.startActivity(this, intent, options.toBundle());
                } else {
                    startActivity(intent);
                }
                break;
            case R.id.score_apps_button:
                Toast.makeText(context, "This Button will launch my Score App", Toast.LENGTH_SHORT).show();
                break;
            case R.id.library_apps_button:
                Toast.makeText(context, "This Button will launch my Library App", Toast.LENGTH_SHORT).show();
                break;
            case R.id.build_it_bigger_button:
                Toast.makeText(context, "This Button will launch my Build It Bigger App", Toast.LENGTH_SHORT).show();
                break;
            case R.id.xyz_reader_button:
                Toast.makeText(context, "This Button will launch my XYZ Reader App", Toast.LENGTH_SHORT).show();
                break;
            case R.id.my_app_button:
                Toast.makeText(context, "This Button will launch my Capstone App", Toast.LENGTH_SHORT).show();
        }
    }
}
