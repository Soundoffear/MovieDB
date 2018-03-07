package com.example.soundoffear.moviedb.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.soundoffear.moviedb.R;
import com.example.soundoffear.moviedb.model.TrailerData;

import java.util.List;

public class TrailersRecViewAdapter extends RecyclerView.Adapter<TrailersRecViewAdapter.TrailersViewHolder> {

    private Context context;

    private List<TrailerData> trailerDataList;

    public TrailersRecViewAdapter(Context context, List<TrailerData> trailerDataList) {
        this.context = context;
        this.trailerDataList = trailerDataList;
    }

    @Override
    public TrailersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutR = R.layout.trailer_item_layout;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View trailersView = layoutInflater.inflate(layoutR, parent, false);

        return new TrailersViewHolder(trailersView);
    }

    @Override
    public void onBindViewHolder(final TrailersViewHolder holder, int position) {
        holder.tv_trailer_name.setText(trailerDataList.get(position).getTrailersName());
        holder.tv_trailer_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playVideo(trailerDataList.get(holder.getAdapterPosition()).getTrailersKey());
            }
        });
    }

    @Override
    public int getItemCount() {
        return trailerDataList.size();
    }

    void playVideo(String videoID) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoID));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videoID));

        try{
            context.startActivity(appIntent);
        } catch (Exception e) {
            context.startActivity(webIntent);
        }
    }

    class TrailersViewHolder extends RecyclerView.ViewHolder {

        Button tv_trailer_name;

        TrailersViewHolder(View trailersView) {
            super(trailersView);
            tv_trailer_name = trailersView.findViewById(R.id.trailer_title);
        }

    }

}
