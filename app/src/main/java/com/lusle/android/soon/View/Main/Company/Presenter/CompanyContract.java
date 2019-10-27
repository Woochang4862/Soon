package com.lusle.android.soon.View.Main.Company.Presenter;

import android.content.Context;

import com.lusle.android.soon.Adapter.Contract.FragmentFavoriteCompanyAdapterContract;
import com.lusle.android.soon.Model.Source.FavoriteCompanyDataLocalSource;

public interface CompanyContract {
    interface View {
        void showErrorToast();

        void showDialog(boolean show);

        void runRecyclerViewAnimation();

        Context getContext();

        void setRecyclerEmpty(boolean visibility);
    }

    interface Presenter {
        void attachView(View view);

        void setAdapterView(FragmentFavoriteCompanyAdapterContract.View adapterView);

        void setAdapterModel(FragmentFavoriteCompanyAdapterContract.Model adapterModel);

        void setModel(FavoriteCompanyDataLocalSource model);

        void detachView();

        void setOnItemClickListener();

        void setOnEmptyListener();

        void loadItems();
    }
}
