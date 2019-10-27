package com.lusle.android.soon.Adapter.Holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lusle.android.soon.Adapter.Listener.OnItemClickListener;
import com.lusle.android.soon.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BookMarkViewHolder extends RecyclerView.ViewHolder {

    public TextView textView;
    public ImageView imageView;

    public BookMarkViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
        super(itemView);
        itemView.setOnClickListener(v -> {
           if(onItemClickListener != null){
               onItemClickListener.onItemClick(v, getLayoutPosition());
           }
        });
        textView = itemView.findViewById(R.id.favorite_recyclerview_item_company_name);
        imageView = itemView.findViewById(R.id.bookmark_recyclerview_item_poster);
    }
}