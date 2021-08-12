package com.lusle.android.soon.View.Main.Company.Presenter;

import android.util.Log;

import com.lusle.android.soon.Adapter.Contract.FragmentFavoriteCompanyAdapterContract;
import com.lusle.android.soon.Adapter.Contract.MovieListRecyclerAdapterContract;
import com.lusle.android.soon.Adapter.Listener.OnItemClickListener;
import com.lusle.android.soon.Model.Contract.GenreDataRemoteSourceContract;
import com.lusle.android.soon.Model.Contract.MovieDataRemoteSourceContract;
import com.lusle.android.soon.Model.Schema.Genre;
import com.lusle.android.soon.Model.Schema.Movie;
import com.lusle.android.soon.Model.Schema.MovieResult;
import com.lusle.android.soon.Model.Source.FavoriteCompanyDataLocalSource;
import com.lusle.android.soon.Model.Source.MovieDataRemoteSource;
import com.lusle.android.soon.Util.Util;

import java.util.ArrayList;

public class CompanyPresenter implements CompanyContract.Presenter, MovieDataRemoteSourceContract.Model.OnFinishedListener, GenreDataRemoteSourceContract.Model.OnFinishedListener {
    private CompanyContract.View view;
    private FragmentFavoriteCompanyAdapterContract.View companyAdapterView;
    private FragmentFavoriteCompanyAdapterContract.Model companyAdapterModel;
    private MovieListRecyclerAdapterContract.View movieAdapterView;
    private MovieListRecyclerAdapterContract.Model movieAdapterModel;
    private FavoriteCompanyDataLocalSource companyModel;
    private MovieDataRemoteSource movieModel;
    private boolean isSetting;

    @Override
    public void attachView(CompanyContract.View view) {
        this.view = view;
    }

    @Override
    public void setCompanyAdapterView(FragmentFavoriteCompanyAdapterContract.View companyAdapterView) {
        this.companyAdapterView = companyAdapterView;
    }

    @Override
    public void setCompanyAdapterModel(FragmentFavoriteCompanyAdapterContract.Model companyAdapterModel) {
        this.companyAdapterModel = companyAdapterModel;
    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public void setOnItemClickListener() {
    }

    @Override
    public void setOnEmptyListener() {
    }

    @Override
    public void loadItems() {
        view.playShimmer(true);
        view.playShimmer(false);
    }

    @Override
    public void loadItems(int page, boolean isSetting) {
        if (view != null)
            view.playShimmer(true);
        this.isSetting = isSetting;
        movieModel.getThisMonthMovieResult(Util.getRegionCode(view.getContext()), movieAdapterModel.getPage());
    }

    @Override
    public void setOnLoadMoreListener() {
        movieAdapterView.setOnLoadMoreListener(() -> {
            Log.d("TAG", "setOnLoadMoreListener: "+movieAdapterModel.getPage());
            loadItems(movieAdapterModel.getPage(), false);
        });
    }

    @Override
    public void setMovieAdapterModel(MovieListRecyclerAdapterContract.Model model) {
        movieAdapterModel = model;
    }

    @Override
    public void setMovieAdapterView(MovieListRecyclerAdapterContract.View view) {
        movieAdapterView = view;
    }

    @Override
    public void setMovieModel(MovieDataRemoteSource model) {
        movieModel = model;
        movieModel.setOnFinishedListener(this);
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        movieAdapterView.setOnItemClickListener(onItemClickListener);
    }

    @Override
    public Movie getItem(int pos) {
        return movieAdapterModel.getItem(pos);
    }

    @Override
    public void setCompanyModel(FavoriteCompanyDataLocalSource companyModel) {
        this.companyModel = companyModel;
    }

    @Override
    public void onFinished(MovieResult movieArrayList) {
        Log.d("TAG", "onFinished: RUN! "+ movieArrayList.getResults());
        if (isSetting) {
            movieAdapterModel.setList(movieArrayList.getResults());
            movieAdapterModel.setItemLimit(movieArrayList.getTotalResults());
        } else {
            movieAdapterModel.addItems(movieArrayList.getResults());
        }
        movieAdapterView.onNotEmpty();
        movieAdapterView.setLoaded();
        if (view != null) {
            view.playShimmer(false);
        }
    }

    @Override
    public void onFinished(ArrayList<Genre> genres) {
        movieAdapterModel.setPage(1);
        if (view != null)
            movieModel.getThisMonthMovieResult(Util.getRegionCode(view.getContext()), movieAdapterModel.getPage());
    }

    @Override
    public void onFailure(Throwable t) {
        if (view != null) {
            view.showErrorToast();
            view.playShimmer(false);
        }
        movieAdapterView.setLoaded();
        movieAdapterView.onEmpty();
    }
}
