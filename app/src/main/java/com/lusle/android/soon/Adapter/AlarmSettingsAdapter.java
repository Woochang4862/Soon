package com.lusle.android.soon.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lusle.android.soon.Adapter.Contract.AlarmSettingAdapterContract;
import com.lusle.android.soon.Adapter.Holder.AlarmViewHolder;
import com.lusle.android.soon.Adapter.Listener.OnAlarmItemClickListener;
import com.lusle.android.soon.Adapter.Listener.OnItemClickListener;
import com.lusle.android.soon.Model.Schema.Alarm;
import com.lusle.android.soon.R;
import com.lusle.android.soon.Util.CircleTransform;
import com.lusle.android.soon.Util.Util;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AlarmSettingsAdapter extends BaseRecyclerAdapter<RecyclerView.ViewHolder> implements AlarmSettingAdapterContract.View, AlarmSettingAdapterContract.Model {

    private ArrayList<Alarm> list;
    private OnAlarmItemClickListener onAlarmItemClickListener;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm, parent, false);
        return new AlarmViewHolder(v, onAlarmItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AlarmViewHolder alarmViewHolder = (AlarmViewHolder) holder;
        Picasso
                .get()
                .load("https://image.tmdb.org/t/p/w500" + list.get(position).getMovie().getPosterPath())
                .centerCrop()
                .transform(new CircleTransform())
                .fit()
                .error(R.drawable.ic_broken_image)
                .into(alarmViewHolder.poster);
        alarmViewHolder.title.setText(list.get(position).getMovie().getTitle());
        alarmViewHolder.releaseDate.setText(list.get(position).getMovie().getReleaseDate());
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(list.get(position).getMovie().getReleaseDate()));
            alarmViewHolder.dateRemaining.setText(Util.calDDay(cal) + "");
        } catch (ParseException e) {
            e.printStackTrace();
            alarmViewHolder.dateRemaining.setText("-");
        }

        Calendar alarmDate = Calendar.getInstance();
        alarmDate.setTimeInMillis(list.get(position).getMilliseconds());
        alarmViewHolder.alarmDate.setText(new SimpleDateFormat("yyyy-MM-dd@HH:mm").format(alarmDate.getTime()));
    }

    @Override
    public int getItemCount() {
        if (list == null) return 0;
        return list.size();
    }

    public void setList(ArrayList<Alarm> list) {
        this.list = list;
    }

    @Override
    public Alarm getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public void setOnAlarmItemClickListener(OnAlarmItemClickListener onAlarmItemClickListener) {
        this.onAlarmItemClickListener = onAlarmItemClickListener;
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onAlarmItemClickListener = onItemClickListener::onItemClick;
    }
}
