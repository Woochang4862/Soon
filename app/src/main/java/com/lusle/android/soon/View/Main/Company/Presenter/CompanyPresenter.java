package com.lusle.android.soon.View.Main.Company.Presenter;

import android.content.Intent;

import com.lusle.android.soon.Adapter.Contract.FragmentFavoriteCompanyAdapterContract;
import com.lusle.android.soon.Adapter.Listener.OnEmptyListener;
import com.lusle.android.soon.Model.Source.FavoriteCompanyDataLocalSource;
import com.lusle.android.soon.View.MovieList.MovieListActivity;

public class CompanyPresenter implements CompanyContract.Presenter {
    private CompanyContract.View view;
    private FragmentFavoriteCompanyAdapterContract.View adapterView;
    private FragmentFavoriteCompanyAdapterContract.Model adapterModel;
    private FavoriteCompanyDataLocalSource model;

    @Override
    public void attachView(CompanyContract.View view) {
        this.view = view;
    }

    @Override
    public void setAdapterView(FragmentFavoriteCompanyAdapterContract.View adapterView) {
        this.adapterView = adapterView;
    }

    @Override
    public void setAdapterModel(FragmentFavoriteCompanyAdapterContract.Model adapterModel) {
        this.adapterModel = adapterModel;
    }

    @Override
    public void detachView() {
        view = null;
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
    public void loadItems() {
        view.showDialog(true);
        adapterModel.setList(model.getFavoriteCompany(view.getContext()));
        view.runRecyclerViewAnimation();
        view.showDialog(false);
    }

    @Override
    public void setModel(FavoriteCompanyDataLocalSource model) {
        this.model = model;
    }
}
