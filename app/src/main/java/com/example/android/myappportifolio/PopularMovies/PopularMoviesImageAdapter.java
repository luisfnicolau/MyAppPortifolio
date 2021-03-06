package com.example.android.myappportifolio.PopularMovies;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.android.myappportifolio.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Luis on 10/26/2015.
 */
public class PopularMoviesImageAdapter extends BaseAdapter {
    private Context context;
    private String[][] movies;
    private ArrayList<String> movieDetail;
    Bitmap[] images;
    ImageView imageView;
    boolean hasInternet = false;

    public PopularMoviesImageAdapter(Context context, String[][] movies, Bitmap[] images, boolean hasInternet) {
        this.context = context;
        this.movies = movies;
        this.hasInternet = hasInternet;
        this.images = images;
    }

    @Override
    public int getCount() {
        return movies.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //if has internet (and new info to download) download new posters
        if (hasInternet) {
            if (convertView == null) {
                imageView = new ImageView(context);
            } else {
                imageView = (ImageView) convertView;
            }

            //Just download new posters if it is not already in memory
            if (images[position] == null) {
                System.out.println("Entrou pra baixar uma nova imagem no Adapter");
                Picasso.with(context)
                        .load(movies[position][1])
                        .placeholder(R.drawable.ic_image_black_24dp)
                        .error(R.drawable.ic_cancel_black_24dp)
                        .into(imageView);
            } else {
                System.out.println("Nao baixou uma nova imagem no Adapter");
                imageView.setImageBitmap(images[position]);
            }

//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setAdjustViewBounds(true);
//            boolean isTablet = context.getResources().getBoolean(R.bool.isTablet);
//            if (isTablet && context.getResources().getConfiguration().orientation == context.getResources().getConfiguration().ORIENTATION_PORTRAIT) {
//                imageView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, PopularMoviesUtility.convertDipToPixels(425, context)));
//            } else if(isTablet && context.getResources().getConfiguration().orientation == context.getResources().getConfiguration().ORIENTATION_LANDSCAPE) {
//                imageView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, PopularMoviesUtility.convertDipToPixels(250, context)));
//            } else {
//                imageView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, PopularMoviesUtility.convertDipToPixels(325, context)));
//            }
            return imageView;
        } else {
            if (convertView == null) {
                imageView = new ImageView(context);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageBitmap(images[position]);
            if (images[position] == null) {
                int j = 0;
                for (int i = 0; i < images.length; i++) {
                    if (images[i] != null) {
                        j = i;
                        break;
                    }
                }
                if (images[j] != null) {
                    imageView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, PopularMoviesUtility.convertDipToPixels(images[j].getHeight(), context)));
                }
                imageView.setImageDrawable(context.getDrawable(R.drawable.ic_cancel_black_24dp));
            } else {
                imageView.setAdjustViewBounds(true);
            }
//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            boolean isTablet = context.getResources().getBoolean(R.bool.isTablet);
//            if (isTablet && context.getResources().getConfiguration().orientation == context.getResources().getConfiguration().ORIENTATION_PORTRAIT) {
//                imageView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, PopularMoviesUtility.convertDipToPixels(425, context)));
//            }else if(isTablet && context.getResources().getConfiguration().orientation == context.getResources().getConfiguration().ORIENTATION_LANDSCAPE) {
//                imageView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, PopularMoviesUtility.convertDipToPixels(250, context)));
//            } else {
//                imageView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, PopularMoviesUtility.convertDipToPixels(325, context)));
//            }
            return imageView;
        }
    }
}


