package com.lusle.android.soon.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lusle.android.soon.Adapter.Contract.FragmentGenreAdapterContract;
import com.lusle.android.soon.Adapter.Holder.GenreViewHolder;
import com.lusle.android.soon.Adapter.Listener.OnItemClickListener;
import com.lusle.android.soon.Model.Schema.Genre;
import com.lusle.android.soon.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FragmentGenreGenreRecyclerViewAdapter extends BaseRecyclerAdapter<RecyclerView.ViewHolder> implements FragmentGenreAdapterContract.View, FragmentGenreAdapterContract.Model {

    private ArrayList<Genre> mList;
    private OnItemClickListener onItemClickListener;
    private String baseUrl = "https://soon-image-server.s3.ap-northeast-2.amazonaws.com";

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_genre_recyclerview, parent, false);
        return new GenreViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d("Genre", "onBindViewHolder");
        Picasso.get()
                .load(baseUrl + mList.get(position).getIcon_path())
                .centerInside()
                .fit()
                .error(R.drawable.ic_broken_image)
                .into(((GenreViewHolder) holder).genreIcon);
        ((GenreViewHolder) holder).genreText.setText(mList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        if (mList == null) return 0;
        return mList.size();
    }

    @Override
    public void setOnItemClickListener(com.lusle.android.soon.Adapter.Listener.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void setList(ArrayList<Genre> mList) {
        this.mList = mList;
    }

    @Override
    public Genre getItem(int position) {
        return mList.get(position);
    }
}
