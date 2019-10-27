package com.lusle.android.soon.View.Main.Genre.Presenter;

import android.content.Context;

import com.lusle.android.soon.Adapter.Contract.FragmentGenreAdapterContract;
import com.lusle.android.soon.Model.Source.GenreDataRemoteSource;

public interface GenreContractor {
    interface View {
        void runRecyclerViewAnimation();

        void showDialog(boolean show);

        void showErrorToast();

        Context getContext();
    }

    interface Presenter {

        void attachView(View view);

        void setAdapterView(FragmentGenreAdapterContract.View adapterView);

        void setAdapterModel(FragmentGenreAdapterContract.Model adapterModel);

        void setOnItemClickListener();

        void loadItems();

        void detachView();

        void setModel(GenreDataRemoteSource model);
    }
}
