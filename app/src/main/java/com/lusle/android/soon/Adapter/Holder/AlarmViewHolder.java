package com.lusle.android.soon.Adapter.Holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lusle.android.soon.Adapter.Listener.OnAlarmItemClickListener;
import com.lusle.android.soon.Model.Schema.Alarm;
import com.lusle.android.soon.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AlarmViewHolder extends RecyclerView.ViewHolder {

    public ImageView poster;
    public TextView title, releaseDate, alarmDate, dateRemaining;

    public AlarmViewHolder(@NonNull View itemView, OnAlarmItemClickListener onAlarmItemClickListener) {
        super(itemView);
        itemView.setOnClickListener(v -> {
            if(onAlarmItemClickListener!=null){
                onAlarmItemClickListener.onItemClick(v,getLayoutPosition());
            }
        });

        poster = itemView.findViewById(R.id.poster);
        title = itemView.findViewById(R.id.title);
        releaseDate = itemView.findViewById(R.id.release_date);
        alarmDate = itemView.findViewById(R.id.alarm_date);
        dateRemaining = itemView.findViewById(R.id.date_remaining);
    }
}
