package com.lusle.android.soon.Adapter.Contract;

import com.lusle.android.soon.Adapter.Listener.OnBookButtonClickListener;
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
        void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener);
        void setOnBookButtonClickListener(OnBookButtonClickListener onBookButtonClickListener);
        void setOnItemClickListener(OnItemClickListener onItemClickListener);
    }
    interface Model {
        void addItems(ArrayList<Movie> list);
        void setList(ArrayList<Movie> list);
        void setGenres(ArrayList<Genre> genres);
        Movie getItem(int i);
        void setItemLimit(int limit);
        int getPage();
        void setPage(int page);
    }
}
