package com.lusle.android.soon.adapter.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lusle.android.soon.adapter.listener.OnItemClickListener;
import com.lusle.android.soon.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReleaseAlarmViewHolder extends RecyclerView.ViewHolder {

    public ImageView poster;
    public TextView title, releaseDate, alarmDate, dateRemaining;

    public ReleaseAlarmViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
        super(itemView);
        itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, getLayoutPosition());
            }
        });

        poster = itemView.findViewById(R.id.poster);
        title = itemView.findViewById(R.id.title);
        releaseDate = itemView.findViewById(R.id.release_date);
        alarmDate = itemView.findViewById(R.id.alarm_date);
        dateRemaining = itemView.findViewById(R.id.date_remaining);
    }
}
