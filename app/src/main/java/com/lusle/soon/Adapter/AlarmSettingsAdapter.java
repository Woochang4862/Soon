package com.lusle.soon.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lusle.soon.Model.Alarm;
import com.lusle.soon.R;
import com.lusle.soon.Utils.CircleTransform;
import com.lusle.soon.Utils.Utils;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AlarmSettingsAdapter extends BaseRecyclerAdapter<RecyclerView.ViewHolder> {

    private ArrayList<Alarm> list;
    private OnItemClickListener onItemClickListener;

    public class AlarmViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView poster;
        private TextView title, releaseDate, alarmDate, dateRemaining;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this::onClick);

            poster = itemView.findViewById(R.id.poster);
            title = itemView.findViewById(R.id.title);
            releaseDate = itemView.findViewById(R.id.release_date);
            alarmDate = itemView.findViewById(R.id.alarm_date);
            dateRemaining = itemView.findViewById(R.id.date_remaining);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null)
                onItemClickListener.onItemClick(v, getLayoutPosition(), list.get(getLayoutPosition()));
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm, parent, false);
        return new AlarmViewHolder(v);
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
            alarmViewHolder.dateRemaining.setText(Utils.calDDay(cal)+"");
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

    public interface OnItemClickListener {
        void onItemClick(View v, int pos, Alarm alarm);
    }

    public void setList(ArrayList<Alarm> list) {
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
