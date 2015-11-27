package com.example.android.myappportifolio;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.Bind;

/**
 * A placeholder fragment containing a simple view.
 */
public class StartActivityFragment extends Fragment{

    Context context;
    @Bind(R.id.spotify_streamer_button) Button spotifyStreamer;
    @Bind(R.id.score_apps_button) Button scoresApp;
    @Bind(R.id.library_apps_button) Button libraryApp;
    @Bind(R.id.build_it_bigger_button) Button buildItBigger;
    @Bind(R.id.xyz_reader_button) Button xyzReader;
    @Bind(R.id.my_app_button) Button myApp;

    //Just for knowledge : )

    public StartActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_start, container, false);
    }
}
