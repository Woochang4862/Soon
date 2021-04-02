package com.lusle.android.soon.Adapter.Holder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.lusle.android.soon.Adapter.Listener.OnItemClickListener;
import com.lusle.android.soon.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MovieViewHolder extends RecyclerView.ViewHolder {
    public ImageView imageView;
    public LottieAnimationView lav;

    public TextView title, adult, genre, overview, release;
    public Button bookBtn;

    public MovieViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
        super(itemView);
        itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, getLayoutPosition());
            }
        });
        imageView = itemView.findViewById(R.id.movie_list_recyclerview_poster);
        lav = itemView.findViewById(R.id.movie_list_recyclerview_poster_empty);
        title = itemView.findViewById(R.id.movie_list_recyclerView_title);
        adult = itemView.findViewById(R.id.movie_list_recyclerview_adult);
        genre = itemView.findViewById(R.id.movie_list_recyclerview_genre);
        overview = itemView.findViewById(R.id.movie_list_recyclerview_overview);
        release = itemView.findViewById(R.id.movie_list_recyclerview_release);
        bookBtn = itemView.findViewById(R.id.movie_list_recyclerview_d_day);
    }
}
