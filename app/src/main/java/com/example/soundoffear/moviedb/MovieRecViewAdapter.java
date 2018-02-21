package com.example.soundoffear.moviedb;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.soundoffear.moviedb.interfaces.OnClickRecView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by soundoffear on 20/02/2018.
 */

public class MovieRecViewAdapter extends RecyclerView.Adapter<MovieRecViewAdapter.MovieRecViewHolder> {

    private List<String> moviePosterString;
    private Context context;
    private static OnClickRecView onClickRecView;

    MovieRecViewAdapter(Context _context, List<String> moviePosterString, OnClickRecView _onClickRecView) {
        this.moviePosterString = moviePosterString;
        this.context = _context;
        onClickRecView = _onClickRecView;
    }

    @Override
    public MovieRecViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutR = R.layout.movie_item;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(layoutR, parent, false);
        return new  MovieRecViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieRecViewHolder holder, int position) {
        Picasso.with(context).load(moviePosterString.get(position)).into(holder.movieImgView);
    }

    @Override
    public int getItemCount() {
        return moviePosterString.size();
    }

    class MovieRecViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView movieImgView;

        MovieRecViewHolder(View itemView) {
            super(itemView);
            movieImgView = itemView.findViewById(R.id.movie_image_poster);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onClickRecView.onRecyclerViewClick(getLayoutPosition());
        }
    }

}
