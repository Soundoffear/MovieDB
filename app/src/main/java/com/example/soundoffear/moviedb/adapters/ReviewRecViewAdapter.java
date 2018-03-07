package com.example.soundoffear.moviedb.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.soundoffear.moviedb.R;
import com.example.soundoffear.moviedb.model.ReviewData;

import java.util.List;

/**
 * Created by soundoffear on 28/02/2018.
 */

public class ReviewRecViewAdapter extends RecyclerView.Adapter<ReviewRecViewAdapter.ReviewViewHolder> {

    private List<ReviewData> reviewDataList;

    public ReviewRecViewAdapter(List<ReviewData> reviewDataList) {
        this.reviewDataList = reviewDataList;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutR = R.layout.review_item_layout;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View reviewView = layoutInflater.inflate(layoutR, parent,false);

        return new ReviewViewHolder(reviewView);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.tv_review.setText(reviewDataList.get(position).getReview());
        holder.tv_author.setText(reviewDataList.get(position).getAuthor());
    }

    @Override
    public int getItemCount() {
        return reviewDataList.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {

        TextView tv_author;
        TextView tv_review;

        ReviewViewHolder(View reviewItem) {
            super(reviewItem);
            tv_author = reviewItem.findViewById(R.id.review_author);
            tv_review = reviewItem.findViewById(R.id.review_review);
        }

    }

}
