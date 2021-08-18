package com.lusle.android.soon.View.Main.Company.Presenter;

import android.content.Context;

import com.lusle.android.soon.Adapter.Contract.FragmentFavoriteCompanyAdapterContract;
import com.lusle.android.soon.Adapter.Contract.MovieListRecyclerAdapterContract;
import com.lusle.android.soon.Adapter.Listener.OnItemClickListener;
import com.lusle.android.soon.Model.Schema.Movie;
import com.lusle.android.soon.Model.Source.FavoriteCompanyDataLocalSource;
import com.lusle.android.soon.Model.Source.MovieDataRemoteSource;

public interface CompanyContract {
    interface View {
        void showErrorToast();

        //void playShimmer(boolean show);

        Context getContext();
    }

    interface Presenter {
        void attachView(View view);

        void setCompanyAdapterView(FragmentFavoriteCompanyAdapterContract.View companyAdapterView);

        void setCompanyAdapterModel(FragmentFavoriteCompanyAdapterContract.Model companyAdapterModel);

        void setCompanyModel(FavoriteCompanyDataLocalSource companyModel);

        void detachView();

        void setOnItemClickListener();

        void setOnEmptyListener();

        void loadItems();

        void loadItems(int page, boolean isSetting);

        void setOnLoadMoreListener();

        void setMovieAdapterModel(MovieListRecyclerAdapterContract.Model model);

        void setMovieAdapterView(MovieListRecyclerAdapterContract.View view);

        void setMovieModel(MovieDataRemoteSource model);

        void setOnItemClickListener(OnItemClickListener onItemClickListener);

        Movie getItem(int pos);
    }
}
