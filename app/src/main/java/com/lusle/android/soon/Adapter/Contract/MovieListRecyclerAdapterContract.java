package com.lusle.android.soon.Adapter.Contract;

import com.lusle.android.soon.Adapter.Listener.OnEmptyListener;
import com.lusle.android.soon.Adapter.Listener.OnItemClickListener;
import com.lusle.android.soon.Adapter.Listener.OnLoadMoreListener;
import com.lusle.android.soon.Model.Schema.Movie;

import java.util.List;

public interface MovieListRecyclerAdapterContract {
    interface View {
        void notifyAdapter();
        void onEmpty();
        void onNotEmpty();
        void setLoaded();
    }
    interface Model {
        void addItems(List<Movie> list);
        void setList(List<Movie> list);
        Movie getItem(int i);
        void setItemLimit(int limit);
        void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener);

        void setOnItemClickListener(OnItemClickListener onItemClickListener);
        int getPage();
        void setPage(int page);
    }
}
