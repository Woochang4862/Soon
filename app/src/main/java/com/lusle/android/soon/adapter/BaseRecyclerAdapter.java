package com.lusle.android.soon.adapter;

import android.util.Log;

import com.lusle.android.soon.adapter.Listener.OnEmptyListener;
import com.lusle.android.soon.adapter.Listener.OnLoadMoreListener;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
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
    protected int page = 1;

    public BaseRecyclerAdapter() {
    }

    public BaseRecyclerAdapter(RecyclerView recyclerView) {
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            final GridLayoutManager linearLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
            if (recyclerView.getRootView().getRootView() instanceof NestedScrollView) {
                ((NestedScrollView) recyclerView.getRootView().getRootView()).setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                        if (scrollY < oldScrollY) {
                            Log.i("TAG", "Scroll UP");
                        }

                        if (scrollY == 0) {
                            Log.i("TAG", "TOP SCROLL");
                        }

                        if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) && scrollY > oldScrollY) {
                            Log.i("TAG", "BOTTOM SCROLL");
                            // here where the trick is going
                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                            if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                // End has been reached
                                // Do something
                                if (onLoadMoreListener != null && limit > totalItemCount - 1) {
                                    page++;
                                    loading = true;
                                    onLoadMoreListener.onLoadMore();
                                }
                            }
                        }
                    }
                });
            } else {
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        totalItemCount = linearLayoutManager.getItemCount();
                        lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                        Log.d("onScrolled", "loading : " + loading + ", totalItemCount : " + totalItemCount + ", lastVisibleItem : " + lastVisibleItem + ", visibleThreshold : " + visibleThreshold + " => " + String.valueOf(totalItemCount <= (lastVisibleItem + visibleThreshold)));
                        if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                            // End has been reached
                            // Do something
                            if (onLoadMoreListener != null && limit > totalItemCount - 1) {
                                page++;
                                loading = true;
                                onLoadMoreListener.onLoadMore();
                            }
                        }
                    }
                });
            }
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
        Log.d("onScrolled", loading + "");
    }

    public void setItemLimit(int limit) {
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
