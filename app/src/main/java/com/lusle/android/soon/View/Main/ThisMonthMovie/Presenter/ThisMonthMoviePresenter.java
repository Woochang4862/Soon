package com.lusle.android.soon.View.Main.ThisMonthMovie.Presenter;

import android.content.Intent;

import com.lusle.android.soon.Adapter.Contract.MovieListRecyclerAdapterContract;
import com.lusle.android.soon.Adapter.Listener.OnEmptyListener;
import com.lusle.android.soon.Adapter.Listener.OnItemClickListener;
import com.lusle.android.soon.Model.Contract.GenreDataRemoteSourceContract;
import com.lusle.android.soon.Model.Contract.MovieDataRemoteSourceContract;
import com.lusle.android.soon.Model.Schema.Genre;
import com.lusle.android.soon.Model.Schema.Movie;
import com.lusle.android.soon.Model.Schema.MovieResult;
import com.lusle.android.soon.Model.Source.GenreDataRemoteSource;
import com.lusle.android.soon.Model.Source.MovieDataRemoteSource;
import com.lusle.android.soon.View.Alarm.AlarmSettingActivity;

import java.util.ArrayList;

public class ThisMonthMoviePresenter implements ThisMonthMovieContract.Presenter, MovieDataRemoteSourceContract.Model.OnFinishedListener, GenreDataRemoteSourceContract.Model.OnFinishedListener {

    private ThisMonthMovieContract.View view;

    private MovieListRecyclerAdapterContract.Model adapterModel;
    private MovieListRecyclerAdapterContract.View adapterView;

    private MovieDataRemoteSource movieModel;
    private GenreDataRemoteSource genreModel;

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
        if(view!=null)
            view.showDialog(true);
        if (this.isSetting = isSetting) {
            genreModel.getGenreList();
        } else {
            movieModel.getThisMonthMovieResult(adapterModel.getPage());
        }
    }

    @Override
    public void setMovieModel(MovieDataRemoteSource movieModel) {
        movieModel.setOnFinishedListener(this);
        this.movieModel = movieModel;
    }

    @Override
    public void setGenreModel(GenreDataRemoteSource genreModel) {
        genreModel.setOnFinishedListener(this);
        this.genreModel = genreModel;
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        adapterModel.setOnItemClickListener(onItemClickListener);
    }

    @Override
    public void setOnEmptyListener() {
        adapterModel.setOnEmptyListener(new OnEmptyListener() {
            @Override
            public void onEmpty() {
                if(view!=null) view.setRecyclerEmpty(true);
            }

            @Override
            public void onNotEmpty() {
                if(view!=null) view.setRecyclerEmpty(false);
            }
        });
    }

    @Override
    public void setOnLoadMoreListener() {
        adapterModel.setOnLoadMoreListener(() -> {
            loadItems(adapterModel.getPage(), false);
        });
    }

    @Override
    public void setOnBookButtonClickListener() {
        adapterModel.setOnBookButtonClickListener(movie -> {
            Intent intent = new Intent(view.getContext(), AlarmSettingActivity.class);
            intent.putExtra("movie_info", movie);
            view.getContext().startActivity(intent);
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
        if(view!=null) {
            view.runRecyclerViewAnimation();
            view.showDialog(false);
        }
    }

    @Override
    public void onFinished(ArrayList<Genre> genres) {
        adapterModel.setPage(1);
        adapterModel.setGenres(genres);
        movieModel.getThisMonthMovieResult(adapterModel.getPage());
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
