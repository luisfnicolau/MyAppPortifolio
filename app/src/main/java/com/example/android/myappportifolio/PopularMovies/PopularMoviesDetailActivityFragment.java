package com.example.android.myappportifolio.PopularMovies;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.myappportifolio.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class PopularMoviesDetailActivityFragment extends Fragment {

    Context context;
    ListView videosList;
    Button reviewButton;
    Button reviewButtonName;
    Button moreReviews;
    ArrayList<String> movieDetail = null;
    // Array that store the information about the videos and reviews
    ArrayList<String> videosKey;
    ArrayList<String> reviews;
    String id;
    Boolean isFavoriteList;

    public static String MOVIES_LIST_NAME = "movieList";
    public static String PREFERENCE_NAME = "preference";
    public static String BYTE_ARRAY_NAME = "byteArray";

    public PopularMoviesDetailActivityFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_popular_movies_detail, container, false);

        context = getActivity().getApplicationContext();

        ImageView poster = (ImageView) rootView.findViewById(R.id.popular_movies_detail_poster);

        //Get the movie information from previous activity
        String preference = "";
        byte[] byteArray = null;
        Bundle arguments = getArguments();
        if (arguments != null) {
            movieDetail = arguments.getStringArrayList(MOVIES_LIST_NAME);
            preference = arguments.getString(PREFERENCE_NAME);
            byteArray = arguments.getByteArray(BYTE_ARRAY_NAME);
        } else {
            movieDetail = getActivity().getIntent().getStringArrayListExtra(MOVIES_LIST_NAME);
            preference = getActivity().getIntent().getStringExtra(PREFERENCE_NAME);
            byteArray = getActivity().getIntent().getByteArrayExtra(BYTE_ARRAY_NAME);
        }
        if (preference != null && preference.equals("Fav")) {
            isFavoriteList = true;
        } else {
            isFavoriteList = false;
        }

        final ArrayList<String> movieDetailToFavorite = movieDetail;
        if (movieDetail != null) {
            Bitmap image = new BitmapDrawable(String.valueOf(R.drawable.ic_image_black_24dp)).getBitmap();
            if (byteArray != null) {
                image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                poster.setImageBitmap(image);
            } else {
                Picasso.with(context).load(movieDetail.get(1))
                        .placeholder(R.drawable.ic_image_black_24dp)
                        .error(R.drawable.ic_cancel_black_24dp)
                        .into(poster);
            }

            reviewButton = (Button) rootView.findViewById(R.id.reviews_button);
            reviewButtonName = (Button) rootView.findViewById(R.id.reviews_button_name);
            moreReviews = (Button) rootView.findViewById(R.id.more_reviews);
            final Button favoriteButton = (Button) rootView.findViewById(R.id.popular_movies_favorite_button);

            //Launch the movie video when button is pressed
            videosList = (ListView) rootView.findViewById(R.id.trailers_list);
            videosList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    openVideo(videosKey.get(position));
                }
            });

            TextView name = (TextView) rootView.findViewById(R.id.popular_movies_detail_name);
            TextView year = (TextView) rootView.findViewById(R.id.popular_movies_year);
            TextView synopsis = (TextView) rootView.findViewById(R.id.popular_movies_synopsis);
            TextView vote = (TextView) rootView.findViewById(R.id.popular_movies_vote_average);

            //Change the font size if the movie name is too long
            if (movieDetail.get(0).length() >= 24) {
                name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
            } else if (movieDetail.get(0).length() >= 12) {
                name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 45);
            }

            name.setText(movieDetail.get(0));
            synopsis.setText(movieDetail.get(2));
            vote.setText(movieDetail.get(3));

            //Set the launch date as "Not Available" if the API not give the launch date
            if (movieDetail.get(4) != null && movieDetail.get(4).equals("null")) {
                year.setText("Not Available");
            } else {
                year.setText(formatDate(movieDetail.get(4)));
            }

            id = movieDetail.get(5);

            final Bitmap imageFinal = image;

            if (!isFavoriteList) {
                favoriteButton.setText("Mark as Favorite");
            } else {
                favoriteButton.setText("Remove from Favorites");
            }

            //Set favorite button to save a new favorite or to remove if I in favorite list
            favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isFavoriteList) {
                        PopularMoviesUtility.saveFavoriteInfo(context, movieDetail);
                        PopularMoviesUtility.saveFavoriteImage(context, imageFinal, movieDetail.get(5));
                        Toast.makeText(context, "Movie has been favorited", Toast.LENGTH_SHORT).show();
                    } else {
                        PopularMoviesUtility.removeFavorite(context, movieDetail.get(5));
                        Toast.makeText(context, "Movie has been removed", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, PopularMoviesMain.class);
                        startActivity(intent);
                    }
                }
            });
            //Fetch the video links and reviews from API
            downloadVideosAndReviewData download = new downloadVideosAndReviewData();
            download.execute();
        }
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String favorite = preferences.getString(context.getString(R.string.pref_unit_key_popular_movies), context.getString(R.string.pref_unit_label_popular_movies));
        //Restart the Main activity when press back so it can be updated
        if (favorite.equals("Fav")) {
            Intent intent = new Intent(context, PopularMoviesMain.class);
            startActivity(intent);
        }
    }

    //Format date to look better
    private String formatDate(String date) {
        String year = date.substring(0, 4);
        String month = date.substring(5, 7);
        String day = date.substring(8, 10);
        date = month + "/" + day + "/" + year;
        return date;
    }

    //Fetch the video links and reviews from API
    private class downloadVideosAndReviewData extends AsyncTask<Void, Void, ArrayList[]> {

        private final String API_KEY = "5efacb99e47c9cb39f08ce2dc7138c15";
        private final String LOG_TAG = downloadVideosAndReviewData.class.getSimpleName();
        private final String BASE_URL = "api.themoviedb.org";
        private final String FIRST_PARAMETER = "3";
        private final String MOVIE_PARAMETER = "movie";
        private String id_parameter = id;
        private final String VIDEOS_PARAMETER = "videos";
        private final String REVIEWS_PARAMETER = "reviews";
        private final String APY_KEY_IDENTIFIER = "api_key";

        String searchType;

        //Pass a Array of ArrayList to post execute. The ArrayLists passed are the video links ArrayLists and the reviews ArrayList
        @Override
        protected ArrayList[] doInBackground(Void... params) {

            //choose to fetch reviews and videos link alongside, the same process for both changing the API source
            ArrayList<String> videos = null;
            ArrayList<String> reviews = null;

            HttpURLConnection urlVideoConnection = null;
            HttpURLConnection urlReviewConnection = null;
            BufferedReader videoReader = null;
            BufferedReader reviewReader = null;

            String finalIDBMJsonString = null;
            String videosListJsonString = null;
            String reviewListJsonString = null;

            String uriBuild = "Not Initializated";
            //Build URI
            try {

                Uri.Builder videosBuilder = new Uri.Builder();
                videosBuilder.scheme("https")
                        .authority(BASE_URL)
                        .appendPath(FIRST_PARAMETER)
                        .appendPath(MOVIE_PARAMETER)
                        .appendPath(id_parameter)
                        .appendPath(VIDEOS_PARAMETER)
                        .appendQueryParameter(APY_KEY_IDENTIFIER, API_KEY);

                Uri.Builder reviewBuilder = new Uri.Builder();
                reviewBuilder.scheme("https")
                        .authority(BASE_URL)
                        .appendPath(FIRST_PARAMETER)
                        .appendPath(MOVIE_PARAMETER)
                        .appendPath(id_parameter)
                        .appendPath(REVIEWS_PARAMETER)
                        .appendQueryParameter(APY_KEY_IDENTIFIER, API_KEY);

                String videosUrl = videosBuilder.build().toString();
                uriBuild = videosUrl;
                String reviewUrl = reviewBuilder.build().toString();
                URL videosList = new URL(videosUrl);
                URL reviewList = new URL(reviewUrl);

                Log.v(LOG_TAG, "Built Trailer URL: " + videosUrl);
                Log.v(LOG_TAG, "Built Review URL: " + reviewUrl);

                //Create the connection
                urlVideoConnection = (HttpURLConnection) videosList.openConnection();
                urlVideoConnection.setRequestMethod("GET");
                urlVideoConnection.connect();

                urlReviewConnection = (HttpURLConnection) reviewList.openConnection();
                urlReviewConnection.setRequestMethod("GET");
                urlReviewConnection.connect();


                //Read the input Stream into a String
                InputStream videoInputStream = urlVideoConnection.getInputStream();
                InputStream reviewInputStream = urlReviewConnection.getInputStream();
                StringBuffer videoBuffer = new StringBuffer();
                StringBuffer reviewBuffer = new StringBuffer();
                if (videoInputStream == null || reviewInputStream == null) {
                    //Nothing was get
                    return null;
                }
                videoReader = new BufferedReader(new InputStreamReader(videoInputStream));
                reviewReader = new BufferedReader(new InputStreamReader(reviewInputStream));

                String line;
                while ((line = videoReader.readLine()) != null) {
                    //Reading the Json file and jump a line just for debuging purposes
                    videoBuffer.append(line + "\n");
                }

                if (videoBuffer.length() == 0) {
                    return null;
                }

                videosListJsonString = videoBuffer.toString();

                while ((line = reviewReader.readLine()) != null) {
                    //Reading the Json file and jump a line just for debuging purposes
                    reviewBuffer.append(line + "\n");
                }

                if (reviewBuffer.length() == 0) {
                    return null;
                }

                reviewListJsonString = reviewBuffer.toString();

                //Get the info from Json file
                try {
                    videos = getVideoInfoFromJson(videosListJsonString);
                    reviews = getReviewInfoFromJson(reviewListJsonString);
                } catch (JSONException j) {
                    Log.e(LOG_TAG, "Coudld not fetch data from Json File");
                }

                Log.v(LOG_TAG, "Movie List Json: " + videosListJsonString);

            } catch (IOException e) {
                Log.v(LOG_TAG, uriBuild);
                Log.v(LOG_TAG, "Error on getting artist Id Json", e);
                return null;
            } finally {
                //Close connection and BufferedReader
                if (urlVideoConnection != null) {
                    urlVideoConnection.disconnect();
                }
                if (urlReviewConnection != null) {
                    urlReviewConnection.disconnect();
                }
                if (videoReader != null && reviewReader != null) {
                    try {
                        videoReader.close();
                        reviewReader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing Stream", e);
                    }
                }
            }
            //Array of ArrayList of video and reviews
            ArrayList<String>[] lists = new ArrayList[2];
            lists[0] = videos;
            lists[1] = reviews;
            return lists;
        }

        @Override
        protected void onPostExecute(ArrayList[] lists) {
            super.onPostExecute(lists);
            //Check if the lists are passed alright
            if (lists != null) {
                //Recover the ArrayLists
                ArrayList<String> videos = lists[0];
                reviews = lists[1];

                // Adapter for videos list
                videosKey = videos;
                ArrayList<String> videosNum = new ArrayList<>();
                for (int i = 1; i <= videos.size(); i++) {
                    videosNum.add("Video " + i);
                }
                if (videos != null) {
                    VideosListAdapter videosNameAdapter = new VideosListAdapter(context, videos);
                    videosList.setAdapter(videosNameAdapter);
                    //Set the size to make the ListView have the size of its children added. Made that because I already using a ScroolView as a parent of my view.
                    if (videos.size() != 0) {
                        videosList.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, videosNameAdapter.getCount() * PopularMoviesUtility.convertDipToPixels(75, context)));
                        setVideoUrlToShare(videosKey.get(0));
                    }
                    else {
                        videosList.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    }
                }

                //Set review text and buttons
                if (reviews != null && reviews.size() != 0) {
                    reviewButtonName.setText(reviews.get(0));
                    reviewButton.setText(reviews.get(1));
                    //Pass the list of reviews to parent activity so it can be handled when clicked (I choose to use that instead a ClickListener, seen easier to me in this case)
                    PopularMoviesDetailActivity.setReviews(reviews);
                } else {
                    //Put a placeholder text if there are no reviews
                    reviewButtonName.setText("No Reviews");
                    reviewButtonName.setEnabled(false);
                    reviewButton.setVisibility(View.GONE);
                    moreReviews.setVisibility(View.GONE);
                }
            }
        }
    }

    //Get video data from Json
    private ArrayList<String> getVideoInfoFromJson(String videosInfoJson) throws JSONException {

        ArrayList<String> videos = new ArrayList<>();

        JSONObject moviesJsonFile = new JSONObject(videosInfoJson);

        JSONArray moviesJsonArray = moviesJsonFile.getJSONArray("results");

        for (int i = 0; i < moviesJsonArray.length(); i++) {
            videos.add(moviesJsonArray.getJSONObject(i).getString("key"));
        }
        return videos;
    }

    //Get review data from Json
    private ArrayList<String> getReviewInfoFromJson(String reviewInfoJson) throws JSONException {

        ArrayList<String> reviews = new ArrayList<>();

        JSONObject reviewJsonFile = new JSONObject(reviewInfoJson);

        JSONArray reviewJsonArray = reviewJsonFile.getJSONArray("results");

        for (int i = 0; i < reviewJsonArray.length(); i++) {
            reviews.add(reviewJsonArray.getJSONObject(i).getString("author"));
            reviews.add(reviewJsonArray.getJSONObject(i).getString("content"));
        }
        return reviews;
    }

    //Videos list adapter
    private class VideosListAdapter extends ArrayAdapter {

        ArrayList<String> movieList;

        public VideosListAdapter(Context context, ArrayList<String> list) {
            super(context, 0, list);
            movieList = list;
        }

        //Prepare to set a placeholder text if there are no videos
        @Override
        public int getCount() {
            if (movieList.size() == 0) {
                return 1;
            } else {
                return super.getCount();
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.popular_movies_trailer_list_item, parent, false);
            }

            TextView text = (TextView) convertView.findViewById(R.id.popular_movies_videos_text_view);
            ImageView image = (ImageView) convertView.findViewById(R.id.popular_movies_videos_image_view);

            //Set a placeholder text if there are no videos and make some stylish changes to set the text similar to the review text
            if (movieList.size() == 0) {
                text.setText("No trailers available");
                image.setVisibility(View.GONE);
                text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                text.setTextColor(Color.parseColor("#B2B2B2"));
                text.setPadding(PopularMoviesUtility.convertDipToPixels(8, context), PopularMoviesUtility.convertDipToPixels(8, context), 0, 0);
            } else {
                text.setText("Trailer " + (position + 1));
                image.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            }
            return convertView;
        }

        //Disable the click option if there are no videos
        @Override
        public boolean isEnabled(int position) {
            if (movieList.size() != 0) {
                return super.isEnabled(position);
            } else {
                return false;
            }
        }
    }

    // Function that opens the web video
    private void openVideo(String url) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("www.youtube.com")
                .appendPath("watch")
                .appendQueryParameter("v", url);
        String myUrl = builder.build().toString();
        Uri videoPage = Uri.parse(myUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, videoPage);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void setVideoUrlToShare (String videoUrl) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("www.youtube.com")
                .appendPath("watch")
                .appendQueryParameter("v", videoUrl);
        String myUrl = builder.build().toString();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, myUrl);
        sendIntent.setType("text/plain");
        PopularMoviesDetailActivity.setShareIntent(sendIntent);
    }
}
