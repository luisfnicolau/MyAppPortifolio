package com.example.android.myappportifolio.PopularMovies;

import android.accounts.Account;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.android.myappportifolio.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */


public class PopularMoviesMainFragment extends Fragment {

    Context context;
    String[][] movies;  //Variable that save all the information aboute the movies
    GridView popularMoviesGrid;
    String preference;
    Bitmap[] images;
    boolean needReloadImages = false;



    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ContentResolver mResolver = getActivity().getApplicationContext().getContentResolver();
        mResolver.addPeriodicSync(new Account("a", "b"), "autorithy", Bundle.EMPTY, 2L);

        final View rootView = inflater.inflate(R.layout.fragment_popular_movies_main, container, false);

        context = getActivity().getApplicationContext();

        popularMoviesGrid = (GridView) rootView.findViewById(R.id.popular_movies_grid_view);

        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet) {
            popularMoviesGrid.setNumColumns(2);
        }

        //Get the preferences to sort the movies
//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
//        preference = sharedPref.getString(context.getString(R.string.pref_unit_key_popular_movies), context.getString(R.string.pref_unit_label_popular_movies));

//        SharedPreferences sharedPref = getActivity().getPreferences(context.MODE_PRIVATE);
//        String defaultValue = getResources().getString(R.string.pref_movies_default);
//        preference = sharedPref.getString(getString(R.string.pref_movies_key), defaultValue);

        preference = PopularMoviesUtility.getPreference(context, getActivity());



        //If user chose Most Popular Movies go in here
        if (preference.equals("Popular")) {

            String tag = preference;
            movies = PopularMoviesUtility.loadInfo(context, tag);
            images = PopularMoviesUtility.loadImages(context, tag);
            if (movies != null) {
                //if images == null so go to adapter to download the posters
                if (images == null) {
                    PopularMoviesImageAdapter imageAdapter = new PopularMoviesImageAdapter(context, movies, images, true);
                    popularMoviesGrid.setAdapter(imageAdapter);
                } else { // else just fetch the posters from device memory
                    PopularMoviesImageAdapter imageAdapter = new PopularMoviesImageAdapter(context, movies, images, false);
                    popularMoviesGrid.setAdapter(imageAdapter);
                }
            }
            downloadMovieData download = new downloadMovieData();
            download.execute();


        } else if (preference.equals("Rate")) { //If user chose Highest Rated Movies go in here

            String tag = preference;
            movies = PopularMoviesUtility.loadInfo(context, tag);
            images = PopularMoviesUtility.loadImages(context, tag);
            if (movies != null) {
                //if images == null so go to adapter to download the posters
                if (images == null) {
                    PopularMoviesImageAdapter imageAdapter = new PopularMoviesImageAdapter(context, movies, images, true);
                    popularMoviesGrid.setAdapter(imageAdapter);
                } else { // else just fetch the posters from device memory
                    PopularMoviesImageAdapter imageAdapter = new PopularMoviesImageAdapter(context, movies, images, false);
                    popularMoviesGrid.setAdapter(imageAdapter);
                }
            }
            downloadMovieData download = new downloadMovieData();
            download.execute();
        } else if (preference.equals("Fav")) {         //If user chosse Favorited Movies go in here

            movies = PopularMoviesUtility.loadFavoriteInfo(context);
            String[] moviesID = new String[movies.length];
            //Get the id from the movies so I can get the saved posters
            for (int i = 0; i < movies.length; i++) {
                moviesID[i] = movies[i][5];
            }
            images = PopularMoviesUtility.loadFavoriteImage(context, moviesID);
            PopularMoviesImageAdapter imageAdapter = new PopularMoviesImageAdapter(context, movies, images, false);
            popularMoviesGrid.setAdapter(imageAdapter);
        }


        // Move to a detail view when movie poster is clicked
            popularMoviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (movies[position][5] != null) {
                        ArrayList<String> movieDetail = new ArrayList<String>();
                        //the Array "images" is not reloaded after new images was fetch from internet and saved on the device, so I load here, with a boolean to check if is necessary
                        if (needReloadImages) {
                            images = PopularMoviesUtility.loadImages(context, preference);
                        }
                        for (int i = 0; i < movies[position].length; i++) {
                            movieDetail.add(movies[position][i]);
                        }
                        Intent intent = new Intent(context, PopularMoviesDetailActivity.class);
                        intent.putExtra("moviesList", movieDetail);
                        //Chek if I am at favorite list to make some changes in detail
                        if (preference.equals("Fav")) {
                            intent.putExtra("FavoriteList", true);
                        } else {
                            intent.putExtra("FavoriteList", false);
                        }

                        //Convert to byte array
                        if (images[position] != null) {
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            images[position].compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byte[] byteArray = stream.toByteArray();
                            intent.putExtra("moviePoster", byteArray);
                        }

                        //Check if is tablet (yet to be implemented)
                        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
                            startActivity(intent);
                    }
                }
            });
        return rootView;
    }

// Download de data from themoviedb API
private class downloadMovieData extends AsyncTask<Void, Void, Boolean> {

    private final String API_KEY = "5efacb99e47c9cb39f08ce2dc7138c15";
    private final String LOG_TAG = downloadMovieData.class.getSimpleName();
    private final String BASE_URL = "api.themoviedb.org";
    private final String FIRST_PARAMETER = "3";
    private final String DISCOVER_PARAMETER = "discover";
    private final String MOVIE_PARAMETER = "movie";
    private final String SORT_PARAMETER_IDENTIFIER = "sort_by";
    private final String APY_KEY_IDENTIFIER = "api_key";

    String searchType;


    @Override
    protected Boolean doInBackground(Void... params) {

        Boolean hasInternet = new Boolean("true");

        //Check preferences to fetch the right data
        if (preference.equals("Rate")) {
            searchType = "vote_average.desc";
        } else {
            searchType = "popularity.desc";
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String finalIDBMJsonString = null;
        String movieListJsonString = null;

        //Buold URI
        try {

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority(BASE_URL)
                    .appendPath(FIRST_PARAMETER)
                    .appendPath(DISCOVER_PARAMETER)
                    .appendPath(MOVIE_PARAMETER)
                    .appendQueryParameter(SORT_PARAMETER_IDENTIFIER, searchType)
                    .appendQueryParameter(APY_KEY_IDENTIFIER, API_KEY);
            ;

            String myUrl = builder.build().toString();
            URL movieList = new URL(myUrl);

            Log.v(LOG_TAG, "Built URL: " + myUrl);

            //Create the connection
            urlConnection = (HttpURLConnection) movieList.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();


            //Read the input Stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                //Nothing was get
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                //Reading the Json file and jump a line just for debuging purposes
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }

            movieListJsonString = buffer.toString();
            //Get the info from Json file
            try {
                movies = getInfoFromJson(movieListJsonString);
            } catch (JSONException j) {
                Log.e(LOG_TAG, "Coudld not fetch data from Json File");
            }

            Log.v(LOG_TAG, "Movie List Json: " + movieListJsonString);

            //Build the path to movie poster
            builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("image.tmdb.org")
                    .appendPath("t")
                    .appendPath("p")
                    .appendPath("w185");

            myUrl = builder.build().toString();

            //Add the fetched information on an array
            for (int i = 0; i < movies.length; i++) {
                if (!movies[i][1].equals("null")) {
                    movies[i][1] = myUrl + movies[i][1];
                }
            }
            Log.v(LOG_TAG, movies[0][1]);
        } catch (IOException e) {
            Log.v(LOG_TAG, "Error on getting artist Id Json", e);
            hasInternet = new Boolean("false");
        } finally {
            //Close connection and BufferedReader
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing Stream", e);
                }
            }
        }
        return hasInternet;

    }

    //Set the adapter to the GridView
    @Override
    protected void onPostExecute(Boolean hasInternet) {
        super.onPostExecute(hasInternet);
        Bitmap[] images = PopularMoviesUtility.loadImages(context, preference);
        boolean hasNet = hasInternet.booleanValue();
        boolean hasNewInfo = false;
        ArrayList<String> oldInfo = new ArrayList<>();
        String[][] movieInfo = PopularMoviesUtility.loadInfo(context, preference);
        for (int i = 0; i < movies.length; i++) {
            if (hasNet && (!movies[i][5].equals(movieInfo[i][5]))) {
                hasNewInfo = true;
            }
        }
        if (hasNewInfo) {
            //Check here what the diferences between the new info fetched and the info saved and update only the diferences without needing to download more images than the necessary
            if (images != null && movieInfo != null) {
                boolean checker;
                for (int i = 0; i < movies.length; i++) {
                    checker = false;
                    for (int j = i; j < movieInfo.length; j++) {
                        if (movies[i][5].equals(movieInfo[j][5])) {
                            Bitmap holder = images[j];
                            Bitmap holder2 = images[i];
                            images[j] = holder2;
                            images[i] = holder;
                            checker = true;
                        }
                    }
                    if (checker == false) {
                        images[i] = null;

                    }
                }

                //Get the info that is not usefull anymore to erase from the device
                for (int i = 0; i < movieInfo.length; i++) {
                    for (int j = 0; j < movies.length; j++) {
                        checker = false;
                        if (movies[j][5].equals(movieInfo[i][5])) {
                            checker = true;
                        }
                    }
                    if (checker = false) {
                        oldInfo.add(movieInfo[i][5]);
                    }
                }
            }
            PopularMoviesImageAdapter imageAdapter = new PopularMoviesImageAdapter(context, movies, images, hasNet);
            popularMoviesGrid.setAdapter(imageAdapter);
            PopularMoviesUtility.DownloadImage(movies, context, preference);
            PopularMoviesUtility.DeleteOldInfo(context, oldInfo);
            needReloadImages = true;
        } else {
            //Just testing the Database, uncomment below to intiate (it will take me to the error that I get stuck)

                PopularMoviesUtility.putSomethingInDb(context, movieInfo[0], preference);
            PopularMoviesUtility.ReadSomethingFromDb(context, preference);
        }

        if (movies != null && hasNewInfo) {

            for (int i = 0; i < movies.length; i++) {
            }
            PopularMoviesUtility.saveInfo(context, movies, preference);

        } else if (movies == null) {
            Toast.makeText(context, "Couldn't connect, please check if you have internet connection", Toast.LENGTH_LONG).show();
        }
    }
}

    //Get data from Json
    private String[][] getInfoFromJson(String moviesInfoJson) throws JSONException {

        String[][] movies = new String[20][6];

        JSONObject moviesJsonFile = new JSONObject(moviesInfoJson);

        JSONArray moviesJsonArray = moviesJsonFile.getJSONArray("results");

        for (int i = 0; i < 20; i++) {
            movies[i][0] = moviesJsonArray.getJSONObject(i).getString("original_title");
            movies[i][1] = moviesJsonArray.getJSONObject(i).getString("poster_path");
            movies[i][2] = moviesJsonArray.getJSONObject(i).getString("overview");
            movies[i][3] = moviesJsonArray.getJSONObject(i).getString("vote_average");
            movies[i][4] = moviesJsonArray.getJSONObject(i).getString("release_date");
            movies[i][5] = moviesJsonArray.getJSONObject(i).getString("id");
        }
        return movies;
    }
}
