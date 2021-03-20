package com.lusle.android.soon.View.Main.Date.Presenter;

import android.content.Context;

import com.lusle.android.soon.Adapter.Contract.MovieListRecyclerAdapterContract;
import com.lusle.android.soon.Model.Source.GenreDataRemoteSource;
import com.lusle.android.soon.Model.Source.MovieDataRemoteSource;

public interface DateContract {
    interface View {

        void showDialog(boolean show);

        void showErrorToast();

        void runRecyclerViewAnimation();

        Context getContext();

        void setRecyclerEmpty(boolean isEmpty);
    }

    interface Presenter {

        void attachView(View view);

        void setGenreModel(GenreDataRemoteSource genreModel);

        void setMovieModel(MovieDataRemoteSource movieModel);

        void setAdapterView(MovieListRecyclerAdapterContract.View adapterView);

        void setAdapterModel(MovieListRecyclerAdapterContract.Model adapterModel);

        void setOnItemClickListener();

        void setOnEmptyListener();

        void setOnLoadMoreListener(String date);

        void setOnBookButtonClickListener();

        void detachView();

        void loadItems(String date, boolean isSetting);
    }
}
