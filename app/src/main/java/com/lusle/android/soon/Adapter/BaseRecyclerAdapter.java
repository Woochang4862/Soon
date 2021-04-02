package com.lusle.android.soon.Adapter;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.lusle.android.soon.Adapter.Listener.OnEmptyListener;
import com.lusle.android.soon.Adapter.Listener.OnLoadMoreListener;
import com.lusle.android.soon.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseRecyclerAdapter<ViewHolder extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<ViewHolder> {
    private OnEmptyListener onEmptyListener;
    private OnLoadMoreListener onLoadMoreListener;
    private int visibleThreshold = 1;
    private int lastVisibleItem, totalItemCount;
    protected boolean loading;
    protected final int VIEW_ITEM = 1;
    protected final int VIEW_PROG = 0;
    protected final int VIEW_AD = 2;
    protected int limit = -1;
    protected int page=1;

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
                    Log.d("onScrolled", "loading : "+loading+
                            ", totalItemCount : "+totalItemCount+
                            ", lastVisibleItem : "+lastVisibleItem+
                            ", visibleThreshold : "+visibleThreshold+" => "+ (totalItemCount <= (lastVisibleItem + visibleThreshold)));
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        // End has been reached
                        // Do something
                        if (onLoadMoreListener != null && limit>totalItemCount-1) {
                            page++;
                            loading = true;
                            onLoadMoreListener.onLoadMore();
                        }
                    }
                }
            });
        }
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

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setLoaded() {
        loading = false;
        Log.d("onScrolled", loading+"");
    }

    public void setItemLimit(int limit){
        this.limit = limit;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void notifyAdapter() {
        notifyDataSetChanged();
    }
}
