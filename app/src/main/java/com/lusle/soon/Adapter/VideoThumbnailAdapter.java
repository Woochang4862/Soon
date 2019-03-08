package com.lusle.soon.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lusle.soon.Model.Video;
import com.lusle.soon.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VideoThumbnailAdapter extends BaseRecyclerAdapter<RecyclerView.ViewHolder> {

    private ArrayList<Video> list;
    private OnClickListener onClickListener;
    private String baseUrl = "https://img.youtube.com/vi/";
    private ArrayList<String> qualityName = new ArrayList<>(Arrays.asList("/maxresdefault.jpg", "/sddefault.jpg", "/mqdefault.jpg", "/hqdefault.jpg", "/default.jpg"));

    public class ThumbnailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView thumbnail;

        public ThumbnailViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            thumbnail = itemView.findViewById(R.id.thumbnail);
        }

        @Override
        public void onClick(View v) {
            if (onClickListener != null)
                onClickListener.onClick(v, getLayoutPosition());
        }

    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_thumbnail, parent, false);
        return new ThumbnailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Picasso
                .get()
                .load(baseUrl + list.get(position).getKey() + qualityName.get(0))
                .fit()
                .centerInside()
                .into(((ThumbnailViewHolder) holder).thumbnail, new Callback() {
                    @Override
                    public void onSuccess() { }
                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(baseUrl + list.get(position).getKey() + qualityName.get(1)).fit().centerInside().into(((ThumbnailViewHolder) holder).thumbnail, new Callback() {
                            @Override
                            public void onSuccess() { }
                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(baseUrl + list.get(position).getKey() + qualityName.get(2)).fit().centerInside().into(((ThumbnailViewHolder) holder).thumbnail, new Callback() {
                                    @Override
                                    public void onSuccess() { }
                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get().load(baseUrl + list.get(position).getKey() + qualityName.get(3)).fit().centerInside().into(((ThumbnailViewHolder) holder).thumbnail, new Callback() {
                                            @Override
                                            public void onSuccess() { }
                                            @Override
                                            public void onError(Exception e) {
                                                Picasso.get().load(baseUrl + list.get(position).getKey() + qualityName.get(4)).fit().centerInside().into(((ThumbnailViewHolder) holder).thumbnail);
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
    }

    @Override
    public int getItemCount() {
        if (list == null) return 0;
        return list.size();
    }

    public interface OnClickListener {

        void onClick(View view, int position);
    }
    public void setList(ArrayList<Video> list) {
        this.list = list;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public Video getItem(int position) {
        return list.get(position);
    }
}
