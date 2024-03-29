package com.lusle.android.soon.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lusle.android.soon.model.schema.Alarm;
import com.lusle.android.soon.R;
import com.lusle.android.soon.util.CircleTransform;
import com.lusle.android.soon.util.Utils;
import com.lusle.android.soon.adapter.holder.ReleaseAlarmViewHolder;
import com.lusle.android.soon.adapter.listener.OnItemClickListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReleaseAlarmSettingsAdapter extends BaseRecyclerAdapter<RecyclerView.ViewHolder> {

    private ArrayList<Alarm> list = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm, parent, false);
        return new ReleaseAlarmViewHolder(v, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ReleaseAlarmViewHolder releaseAlarmViewHolder = (ReleaseAlarmViewHolder) holder;
        Picasso
                .get()
                .load("https://image.tmdb.org/t/p/w500" + list.get(position).getMovie().getPosterPath())
                .centerCrop()
                .transform(new CircleTransform())
                .fit()
                .error(R.drawable.ic_broken_image)
                .into(releaseAlarmViewHolder.poster);
        releaseAlarmViewHolder.title.setText(list.get(position).getMovie().getTitle());
        releaseAlarmViewHolder.releaseDate.setText(list.get(position).getMovie().getReleaseDate());
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(list.get(position).getMovie().getReleaseDate()));
            releaseAlarmViewHolder.dateRemaining.setText(Utils.calDDay(cal) + "");
        } catch (ParseException e) {
            e.printStackTrace();
            releaseAlarmViewHolder.dateRemaining.setText("-");
        }

        Calendar alarmDate = Calendar.getInstance();
        alarmDate.setTimeInMillis(list.get(position).getMilliseconds());
        releaseAlarmViewHolder.alarmDate.setText(new SimpleDateFormat("yyyy-MM-dd@HH:mm").format(alarmDate.getTime()));
    }

    @Override
    public int getItemCount() {
        if (list == null) return 0;
        return list.size();
    }

    public ArrayList<Alarm> getList() { return list; }

    public void setList(ArrayList<Alarm> list) {
        this.list = list;
    }

    public Alarm getItem(int position) {
        return list.get(position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
