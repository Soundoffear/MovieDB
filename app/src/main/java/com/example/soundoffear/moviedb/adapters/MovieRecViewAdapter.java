package com.example.soundoffear.moviedb.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Movie;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.soundoffear.moviedb.R;
import com.example.soundoffear.moviedb.interfaces.OnClickRecView;
import com.example.soundoffear.moviedb.model.ImageData;
import com.squareup.picasso.Picasso;

import java.io.FileInputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MovieRecViewAdapter extends RecyclerView.Adapter<MovieRecViewAdapter.MovieRecViewHolder> {

    private List<ImageData> imageDataList;
    private Context context;
    private static OnClickRecView onClickRecView;
    private boolean isNetwork;

    public MovieRecViewAdapter(Context _context, List<ImageData> imageDataList, OnClickRecView _onClickRecView, boolean isNetwork) {
        this.imageDataList = imageDataList;
        this.context = _context;
        onClickRecView = _onClickRecView;
        this.isNetwork = isNetwork;
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
        if(isNetwork) {
            populateWithNetwork(holder, position);
        } else {
            populateWithOutNetwork(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return imageDataList.size();
    }

    private void populateWithNetwork(MovieRecViewHolder holder, int position) {
        Picasso.with(context).load(imageDataList.get(position).getImageURL()).into(holder.movieImgView);
        String dbMovieID = imageDataList.get(position).getImageID();
        for(int i = 0;i < imageDataList.size(); i++) {
            for(int j = 0; j < imageDataList.get(i).getMovieID().size(); j++) {
                if (imageDataList.get(i).getMovieID().get(j).equals(dbMovieID)) {
                    holder.favImgView.setColorFilter(context.getResources().getColor(R.color.colorAccent));
                }
            }
        }
    }

    private void populateWithOutNetwork(MovieRecViewHolder holder, int position) {
        try {
            FileInputStream fileInputStream = context.openFileInput(imageDataList.get(position).getImageName());
            Bitmap imageBitmap = BitmapFactory.decodeStream(fileInputStream);

            holder.movieImgView.setImageBitmap(imageBitmap);

            for(int i = 0;i < imageDataList.size(); i++) {
                for(int j = 0; j < imageDataList.get(i).getMovieID().size(); j++) {
                    if (imageDataList.get(i).getMovieID().get(j).equals(imageDataList.get(position).getImageID())) {
                        holder.favImgView.setColorFilter(context.getResources().getColor(R.color.colorAccent));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class MovieRecViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView movieImgView;
        ImageView favImgView;

        MovieRecViewHolder(View itemView) {
            super(itemView);
            movieImgView = itemView.findViewById(R.id.movie_image_poster);
            favImgView = itemView.findViewById(R.id.movie_fav_image);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onClickRecView.onRecyclerViewClick(getLayoutPosition());
        }
    }

}
