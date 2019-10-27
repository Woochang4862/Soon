package com.lusle.android.soon.View.Main.ThisMonthMovie.Presenter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.lusle.android.soon.Model.Contract.GenreDataRemoteSourceContract;
import com.lusle.android.soon.Model.Contract.MovieDataRemoteSourceContract;
import com.lusle.android.soon.Model.Source.GenreDataRemoteSource;
import com.lusle.android.soon.View.Alarm.AlarmSettingActivity;
import com.lusle.android.soon.View.Detail.DetailActivity;
import com.lusle.android.soon.Adapter.Contract.MovieListRecyclerAdapterContract;
import com.lusle.android.soon.Adapter.Listener.OnEmptyListener;
import com.lusle.android.soon.Model.Schema.Genre;
import com.lusle.android.soon.Model.Schema.MovieResult;
import com.lusle.android.soon.Model.Source.MovieDataRemoteSource;
import com.lusle.android.soon.R;

import java.util.ArrayList;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;

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
    public void setOnItemClickListener() {
        adapterModel.setOnItemClickListener((v, pos) -> {
            Intent intent = new Intent(view.getContext(), DetailActivity.class);
            intent.putExtra("movie_id", adapterModel.getItem(pos).getId());
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
        view.runRecyclerViewAnimation();
        view.showDialog(false);
    }

    @Override
    public void onFinished(ArrayList<Genre> genres) {
        adapterModel.setPage(1);
        adapterModel.setGenres(genres);
        movieModel.getThisMonthMovieResult(adapterModel.getPage());
    }

    @Override
    public void onFailure(Throwable t) {
        view.showErrorToast();
        adapterView.setLoaded();
        adapterView.onEmpty();
        view.showDialog(false);
    }
}
