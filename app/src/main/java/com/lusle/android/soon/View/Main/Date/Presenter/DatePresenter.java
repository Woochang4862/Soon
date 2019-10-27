package com.lusle.android.soon.View.Main.Date.Presenter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.lusle.android.soon.Adapter.Contract.MovieListRecyclerAdapterContract;
import com.lusle.android.soon.Adapter.Listener.OnBookButtonClickListener;
import com.lusle.android.soon.Adapter.Listener.OnEmptyListener;
import com.lusle.android.soon.Adapter.Listener.OnItemClickListener;
import com.lusle.android.soon.Adapter.Listener.OnLoadMoreListener;
import com.lusle.android.soon.Model.Contract.GenreDataRemoteSourceContract;
import com.lusle.android.soon.Model.Contract.MovieDataRemoteSourceContract;
import com.lusle.android.soon.Model.Schema.Genre;
import com.lusle.android.soon.Model.Schema.Movie;
import com.lusle.android.soon.Model.Schema.MovieResult;
import com.lusle.android.soon.Model.Source.GenreDataRemoteSource;
import com.lusle.android.soon.Model.Source.MovieDataRemoteSource;
import com.lusle.android.soon.R;
import com.lusle.android.soon.View.Alarm.AlarmSettingActivity;
import com.lusle.android.soon.View.Detail.DetailActivity;

import java.util.ArrayList;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;

public class DatePresenter implements DateContract.Presenter, MovieDataRemoteSourceContract.Model.OnFinishedListener, GenreDataRemoteSourceContract.Model.OnFinishedListener {
    private DateContract.View view;
    private GenreDataRemoteSource genreModel;
    private MovieDataRemoteSource movieModel;
    private MovieListRecyclerAdapterContract.View adapterView;
    private MovieListRecyclerAdapterContract.Model adapterModel;
    private boolean isSetting;
    private String date;

    @Override
    public void attachView(DateContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public void setAdapterView(MovieListRecyclerAdapterContract.View adapterView) {
        this.adapterView = adapterView;
    }

    @Override
    public void setAdapterModel(MovieListRecyclerAdapterContract.Model adapterModel) {
        this.adapterModel = adapterModel;
    }

    @Override
    public void setOnItemClickListener() {
        adapterModel.setOnItemClickListener((v, position) -> {
            Intent intent = new Intent(view.getContext(), DetailActivity.class);
            intent.putExtra("movie_id", adapterModel.getItem(position).getId());
            Pair<View, String> poster = Pair.create(v.findViewById(R.id.movie_list_recyclerview_poster), ViewCompat.getTransitionName(v.findViewById(R.id.movie_list_recyclerview_poster)));
            Pair<View, String> title = Pair.create(v.findViewById(R.id.movie_list_recyclerView_title), ViewCompat.getTransitionName(v.findViewById(R.id.movie_list_recyclerView_title)));
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) view.getContext(), poster, title);
            view.getContext().startActivity(intent, options.toBundle());
        });
    }

    @Override
    public void setOnEmptyListener() {
        adapterModel.setOnEmptyListener(new OnEmptyListener() {
            @Override
            public void onEmpty() {
                view.setRecyclerEmpty(true);
            }

            @Override
            public void onNotEmpty() {
                view.setRecyclerEmpty(false);
            }
        });
    }

    @Override
    public void setOnLoadMoreListener(String date) {
        adapterModel.setOnLoadMoreListener(() -> loadItems(date, false));
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
    public void setGenreModel(GenreDataRemoteSource genreModel) {
        genreModel.setOnFinishedListener(this);
        this.genreModel = genreModel;
    }

    @Override
    public void setMovieModel(MovieDataRemoteSource movieModel) {
        movieModel.setOnFinishedListener(this);
        this.movieModel = movieModel;
    }

    @Override
    public void loadItems(String date, boolean isSetting) {
        if (this.isSetting = isSetting) {
            view.showDialog(true);
            adapterModel.setPage(1);
            this.date = date;
            genreModel.getGenreList();
        } else {
            view.showDialog(true);
            movieModel.discoverMovieWithDate(date, adapterModel.getPage());
        }
    }

    @Override
    public void onFinished(MovieResult movieArrayList) {
        if(isSetting) {
            adapterModel.setList(movieArrayList.getResults());
            adapterModel.setItemLimit(movieArrayList.getTotalResults());
        } else {
            adapterModel.addItems(movieArrayList.getResults());
        }
        adapterView.onNotEmpty();
        adapterView.setLoaded();
        view.runRecyclerViewAnimation();
        view.showDialog(false);
    }

    @Override
    public void onFinished(ArrayList<Genre> genres) {
        adapterModel.setGenres(genres);
        movieModel.discoverMovieWithDate(date, adapterModel.getPage());
    }

    @Override
    public void onFailure(Throwable t) {
        view.showErrorToast();
        adapterView.onEmpty();
        adapterView.setLoaded();
        view.showDialog(false);
    }
}
