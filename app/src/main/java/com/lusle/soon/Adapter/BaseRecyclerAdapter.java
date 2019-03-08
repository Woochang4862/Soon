package com.lusle.soon.Adapter;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.lusle.soon.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseRecyclerAdapter<ViewHolder extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<ViewHolder> {
    private OnEmptyListener onEmptyListener;
    private OnLoadMoreListener onLoadMoreListener;
    private int visibleThreshold = 1;
    private int lastVisibleItem, totalItemCount;
    private RecyclerView recyclerView;
    protected boolean loading;
    protected final int VIEW_ITEM = 1;
    protected final int VIEW_PROG = 0;
    protected int limit = -1;

    public BaseRecyclerAdapter() {
    }

    public BaseRecyclerAdapter(RecyclerView recyclerView) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    Log.d("onScrolled", "totalItemCount : "+totalItemCount+", lastVisibleItem : "+lastVisibleItem+", visibleThreshold : "+visibleThreshold+" => "+String.valueOf(totalItemCount <= (lastVisibleItem + visibleThreshold)));
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        // End has been reached
                        // Do something
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
            this.recyclerView = recyclerView;
        }
    }

    protected static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }

    public interface OnEmptyListener {
        void onEmpty();

        void onNotEmpty();
    }

    public void setOnEmptyListener(OnEmptyListener onEmptyListener) {
        this.onEmptyListener = onEmptyListener;
    }

    public void onEmpty() {
        if (onEmptyListener != null)
            onEmptyListener.onEmpty();
    }

    public void onNotEmpty() {
        if (onEmptyListener != null)
            onEmptyListener.onNotEmpty();
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setLoaded() {
        loading = false;
    }

    public void setItemLimit(int limit){
        this.limit = limit;
    }
}
