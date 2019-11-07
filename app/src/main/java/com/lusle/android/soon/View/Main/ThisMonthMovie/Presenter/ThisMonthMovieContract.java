package com.lusle.android.soon.View.Main.ThisMonthMovie.Presenter;

import android.content.Context;

import com.lusle.android.soon.Adapter.Contract.MovieListRecyclerAdapterContract;
import com.lusle.android.soon.Adapter.Listener.OnItemClickListener;
import com.lusle.android.soon.Model.Schema.Movie;
import com.lusle.android.soon.Model.Source.GenreDataRemoteSource;
import com.lusle.android.soon.Model.Source.MovieDataRemoteSource;

public interface ThisMonthMovieContract {
    interface View {
        void showErrorToast();

        void showDialog(boolean show);

        void runRecyclerViewAnimation();

        Context getContext();

        void setRecyclerEmpty(boolean visibility);
    }

    interface Presenter {
        void attachView(View view);

        void setMovieAdapterModel(MovieListRecyclerAdapterContract.Model adapterModel);

        void setMovieAdapterView(MovieListRecyclerAdapterContract.View adapterView);

        void detachView();

        void loadItems(int page, boolean isSetting);

        void setMovieModel(MovieDataRemoteSource movieModel);

        void setGenreModel(GenreDataRemoteSource genreModel);

        void setOnItemClickListener(OnItemClickListener onItemClickListener);

        void setOnEmptyListener();

        void setOnLoadMoreListener();

        void setOnBookButtonClickListener();

        Movie getItem(int pos);
    }
}
