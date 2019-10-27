package com.lusle.android.soon.View.Main.Genre.Presenter;

import android.content.Intent;

import com.lusle.android.soon.Adapter.Contract.FragmentGenreAdapterContract;
import com.lusle.android.soon.Model.Contract.GenreDataRemoteSourceContract;
import com.lusle.android.soon.Model.Schema.Genre;
import com.lusle.android.soon.Model.Source.GenreDataRemoteSource;
import com.lusle.android.soon.View.MovieList.MovieListActivity;

import java.util.ArrayList;

public class GenrePresenter implements GenreContractor.Presenter, GenreDataRemoteSourceContract.Model.OnFinishedListener {

    private GenreContractor.View view;
    private FragmentGenreAdapterContract.View adapterView;
    private FragmentGenreAdapterContract.Model adapterModel;
    private GenreDataRemoteSource model;

    @Override
    public void attachView(GenreContractor.View view) {
        this.view = view;
    }

    @Override
    public void setAdapterView(FragmentGenreAdapterContract.View adapterView) {
        this.adapterView = adapterView;
    }

    @Override
    public void setAdapterModel(FragmentGenreAdapterContract.Model adapterModel) {
        this.adapterModel = adapterModel;
    }

    @Override
    public void setOnItemClickListener() {
        adapterModel.setOnItemClickListener((v, position) -> {
            Intent intent = new Intent(view.getContext(), MovieListActivity.class);
            intent.putExtra("keyword", adapterModel.getItem(position));
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public void loadItems() {
        view.showDialog(true);
        model.getGenreList();
    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public void setModel(GenreDataRemoteSource model) {
        model.setOnFinishedListener(this);
        this.model = model;
    }

    @Override
    public void onFinished(ArrayList<Genre> genres) {
        adapterModel.setList(genres);
        view.runRecyclerViewAnimation();
        adapterView.onNotEmpty();
        view.showDialog(false);
    }

    @Override
    public void onFailure(Throwable t) {
        view.showErrorToast();
        adapterView.onEmpty();
        view.showDialog(false);
    }
}
