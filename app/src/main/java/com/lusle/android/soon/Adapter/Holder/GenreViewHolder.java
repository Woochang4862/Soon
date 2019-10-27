package com.lusle.android.soon.Adapter.Holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lusle.android.soon.Adapter.Listener.OnItemClickListener;
import com.lusle.android.soon.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GenreViewHolder extends RecyclerView.ViewHolder{

    public ImageView genreIcon;
    public TextView genreText;

    public GenreViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
        super(itemView);
        itemView.setOnClickListener(v -> {
            if(onItemClickListener!=null){
                onItemClickListener.onItemClick(v, getLayoutPosition());
            }
        });

        genreIcon = itemView.findViewById(R.id.genre_icon);
        genreText = itemView.findViewById(R.id.genre_text);
    }
}