package com.lusle.android.soon.Adapter;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lusle.android.soon.Adapter.Contract.MovieListRecyclerAdapterContract;
import com.lusle.android.soon.Adapter.Holder.MovieViewHolder;
import com.lusle.android.soon.Adapter.Listener.OnBookButtonClickListener;
import com.lusle.android.soon.Adapter.Listener.OnItemClickListener;
import com.lusle.android.soon.Adapter.Holder.ProgressViewHolder;
import com.lusle.android.soon.Model.Schema.Genre;
import com.lusle.android.soon.Model.Schema.Movie;
import com.lusle.android.soon.R;
import com.lusle.android.soon.Util.Util;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MovieListRecyclerViewAdapter extends BaseRecyclerAdapter<RecyclerView.ViewHolder> implements MovieListRecyclerAdapterContract.Model, MovieListRecyclerAdapterContract.View {

    private ArrayList<Movie> mList;
    private Map<Integer, String> genres;
    private OnItemClickListener onItemClickListener;
    private OnBookButtonClickListener onBookButtonClickListener;

    public MovieListRecyclerViewAdapter(RecyclerView recyclerView) {
        super(recyclerView);
        genres = new HashMap<>();
    }

    @Override
    public int getItemViewType(int position) {
        return (position < mList.size()) ? VIEW_ITEM : VIEW_PROG;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_PROG) {
            Log.d("Ads", "onCreateViewHolder: VIEW_PROG");
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar_recyclerview_load_more, parent, false);
            vh = new ProgressViewHolder(v);
        } else {
            Log.d("Ads", "onCreateViewHolder: VIEW_etc");
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_recyclerview, parent, false);
            vh = new MovieViewHolder(view, onItemClickListener);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MovieViewHolder) {
            Picasso
                    .get()
                    .load("https://image.tmdb.org/t/p/w500" + mList.get(position).getPosterPath())
                    .centerCrop()
                    .fit()
                    .into(((MovieViewHolder) holder).imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            ((MovieViewHolder) holder).imageView.setVisibility(View.VISIBLE);
                            ((MovieViewHolder) holder).lav.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            ((MovieViewHolder) holder).imageView.setVisibility(View.GONE);
                            ((MovieViewHolder) holder).lav.setVisibility(View.VISIBLE);
                            ((MovieViewHolder) holder).lav.setProgress(0);
                            ((MovieViewHolder) holder).lav.playAnimation();
                        }
                    });


            ((MovieViewHolder) holder).title.setText(mList.get(position).getTitle());
            ((MovieViewHolder) holder).adult.setVisibility(mList.get(position).getAdult() ? View.VISIBLE : View.GONE);


            ArrayList<String> genreList = new ArrayList<>();
            for (Integer id : mList.get(position).getGenreIds()) {
                genreList.add(this.genres.get(id));
            }
            ((MovieViewHolder) holder).genre.setText(TextUtils.join(",", genreList));

            if (mList.get(position).getOverview().length() <= 40)
                ((MovieViewHolder) holder).overview.setText(mList.get(position).getOverview());
            else
                ((MovieViewHolder) holder).overview.setText(mList.get(position).getOverview().substring(0, 41));
            Log.d("DateFragment", ((MovieViewHolder) holder).overview.getText().toString());

            ((MovieViewHolder) holder).release.setText("개봉일 : " + mList.get(position).getReleaseDate());


            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = sdf.parse(mList.get(position).getReleaseDate());
                Calendar releaseDate = new GregorianCalendar();
                releaseDate.setTime(date);
                int day = Util.calDDay(releaseDate);
                if (day <= 0) {
                    ((MovieViewHolder) holder).bookBtn.setEnabled(false);
                    ((MovieViewHolder) holder).bookBtn.setText("개봉함");
                } else {
                    ((MovieViewHolder) holder).bookBtn.setEnabled(true);
                    ((MovieViewHolder) holder).bookBtn.setText("DAY - " + day);
                    ((MovieViewHolder) holder).bookBtn.setOnClickListener(v -> {
                        if (onBookButtonClickListener != null)
                            onBookButtonClickListener.onBookButtonClicked(mList.get(position));
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                ((MovieViewHolder) holder).bookBtn.setText("---");
                ((MovieViewHolder) holder).bookBtn.setEnabled(false);
            }
        } else {
            if (limit <= position) {
                ((ProgressViewHolder) holder).progressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mList == null) return 0;
        return mList.size() + 1;
    }

    @Override
    public void notifyAdapter() {
        notifyDataSetChanged();
    }

    @Override
    public void setList(ArrayList<Movie> list) {
        mList = list;
    }

    @Override
    public void addItems(ArrayList<Movie> list) {
        this.mList.addAll(list);
    }

    @Override
    public void setGenres(ArrayList<Genre> genres) {
        for (Genre genre : genres) {
            this.genres.put(genre.getId(), genre.getName());
        }
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public Movie getItem(int i) {
        return mList.get(i);
    }

    @Override
    public void setOnBookButtonClickListener(OnBookButtonClickListener onBookButtonClickListener) {
        this.onBookButtonClickListener = onBookButtonClickListener;
    }
}
