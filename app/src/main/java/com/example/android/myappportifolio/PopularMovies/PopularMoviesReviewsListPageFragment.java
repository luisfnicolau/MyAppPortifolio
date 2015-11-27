package com.example.android.myappportifolio.PopularMovies;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.myappportifolio.R;

import java.util.ArrayList;

/**
 * This fragment was created to show all the reviews if a review is clicked on PopularMoviesDetailActovotyFragment
 */
public class PopularMoviesReviewsListPageFragment extends Fragment {

    Context context;

    public PopularMoviesReviewsListPageFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_popular_movies_reviews_list_page, container, false);

        context = getActivity().getApplicationContext();

        //Recover the views and set the adapter
        ArrayList<String> reviewsList = getActivity().getIntent().getStringArrayListExtra("reviewsList");
        ListView reviewsListView = (ListView) rootView.findViewById(R.id.reviews_list);
        ReviewsListAdapter reviewsListAdapter = new ReviewsListAdapter(context, reviewsList);
        reviewsListView.setAdapter(reviewsListAdapter);

        return rootView;
    }

    private class ReviewsListAdapter extends ArrayAdapter {


        ArrayList<String> reviewArrayList;

        public ReviewsListAdapter(Context context, ArrayList<String> list) {
            super(context, 0, list);
            reviewArrayList = list;
        }

        @Override
        public int getCount() {
            if (reviewArrayList == null) {
                return 0;
            } else {
                return reviewArrayList.size() / 2;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.popular_movies_review_list_item, parent, false);
            }
            TextView author = (TextView) convertView.findViewById(R.id.popular_movies_review_author_text_view);
            TextView content = (TextView) convertView.findViewById(R.id.popular_movies_review_content_text_view);


            author.setText(reviewArrayList.get(position * 2));
            content.setText(reviewArrayList.get(position * 2 + 1));


            System.out.println(convertView.getMeasuredHeight());

            return convertView;
        }
    }
}
