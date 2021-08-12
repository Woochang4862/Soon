package com.lusle.android.soon.View.Main.ThisMonthMovie.Presenter;

import com.lusle.android.soon.Adapter.Contract.MovieListRecyclerAdapterContract;
import com.lusle.android.soon.Adapter.Listener.OnItemClickListener;
import com.lusle.android.soon.Model.Contract.GenreDataRemoteSourceContract;
import com.lusle.android.soon.Model.Contract.MovieDataRemoteSourceContract;
import com.lusle.android.soon.Model.Schema.Genre;
import com.lusle.android.soon.Model.Schema.Movie;
import com.lusle.android.soon.Model.Schema.MovieResult;
import com.lusle.android.soon.Model.Source.MovieDataRemoteSource;
import com.lusle.android.soon.Util.Util;

import java.util.ArrayList;

public class ThisMonthMoviePresenter implements ThisMonthMovieContract.Presenter, MovieDataRemoteSourceContract.Model.OnFinishedListener, GenreDataRemoteSourceContract.Model.OnFinishedListener {

    private ThisMonthMovieContract.View view;

    private MovieListRecyclerAdapterContract.Model adapterModel;
    private MovieListRecyclerAdapterContract.View adapterView;

    private MovieDataRemoteSource movieModel;

    private boolean isSetting;

    @Override
    public void attachView(ThisMonthMovieContract.View view) {
        this.view = view;
    }

    @Override
    public void setMovieAdapterModel(MovieListRecyclerAdapterContract.Model adapterModel) {
        this.adapterModel = adapterModel;
    }

    @Override
    public void setMovieAdapterView(MovieListRecyclerAdapterContract.View adapterView) {
        this.adapterView = adapterView;
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    @Override
    public void loadItems(int page, boolean isSetting) {
        if (view != null)
            view.showDialog(true);
        this.isSetting = isSetting;
        movieModel.getThisMonthMovieResult(Util.getRegionCode(view.getContext()), adapterModel.getPage());
    }

    @Override
    public void setMovieModel(MovieDataRemoteSource movieModel) {
        movieModel.setOnFinishedListener(this);
        this.movieModel = movieModel;
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        adapterView.setOnItemClickListener(onItemClickListener);
    }

    @Override
    public void setOnEmptyListener() {
    }

    @Override
    public void setOnLoadMoreListener() {
        adapterView.setOnLoadMoreListener(() -> {
            loadItems(adapterModel.getPage(), false);
        });
    }

    @Override
    public void onFinished(MovieResult movieArrayList) {
        if (isSetting) {
            adapterModel.setList(movieArrayList.getResults());
            adapterModel.setItemLimit(movieArrayList.getTotalResults());
        } else {
            adapterModel.addItems(movieArrayList.getResults());
        }
        adapterView.onNotEmpty();
        adapterView.setLoaded();
        if (view != null) {
            view.runRecyclerViewAnimation();
            view.showDialog(false);
        }
    }

    @Override
    public void onFinished(ArrayList<Genre> genres) {
        adapterModel.setPage(1);
        if (view != null)
            movieModel.getThisMonthMovieResult(Util.getRegionCode(view.getContext()), adapterModel.getPage());
    }

    @Override
    public void onFailure(Throwable t) {
        if (view != null) {
            view.showErrorToast();
            view.showDialog(false);
        }
        adapterView.setLoaded();
        adapterView.onEmpty();
    }

    @Override
    public Movie getItem(int pos) {
        return adapterModel.getItem(pos);
    }
}
