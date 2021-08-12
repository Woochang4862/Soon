package com.lusle.android.soon.Adapter;

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

import java.util.ArrayList;
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
            ((MovieViewHolder) holder).bind(mList.get(position));
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
