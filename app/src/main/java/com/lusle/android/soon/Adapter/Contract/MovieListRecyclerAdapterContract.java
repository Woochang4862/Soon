package com.lusle.android.soon.Adapter.Contract;

import com.lusle.android.soon.Adapter.Listener.OnBookButtonClickListener;
import com.lusle.android.soon.Adapter.Listener.OnEmptyListener;
import com.lusle.android.soon.Adapter.Listener.OnItemClickListener;
import com.lusle.android.soon.Adapter.Listener.OnLoadMoreListener;
import com.lusle.android.soon.Model.Schema.Genre;
import com.lusle.android.soon.Model.Schema.Movie;

import java.util.ArrayList;

public interface MovieListRecyclerAdapterContract {
    interface View {
        void notifyAdapter();
        void onEmpty();
        void onNotEmpty();
        void setLoaded();
    }
    interface Model {
        void addItems(ArrayList<Movie> list);
        void setList(ArrayList<Movie> list);
        Movie getItem(int i);
        void setItemLimit(int limit);
        void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener);
        void setOnEmptyListener(OnEmptyListener onEmptyListener);
        void setGenres(ArrayList<Genre> genres);
        void setOnItemClickListener(OnItemClickListener onItemClickListener);
        void setOnBookButtonClickListener(OnBookButtonClickListener onBookButtonClickListener);
        int getPage();
        void setPage(int page);
    }
}
