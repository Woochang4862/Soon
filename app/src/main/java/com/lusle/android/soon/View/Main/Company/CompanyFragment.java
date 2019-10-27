package com.lusle.android.soon.View.Main.Company;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lusle.android.soon.Adapter.FragmentCompanyFavoriteRecyclerViewAdapter;
import com.lusle.android.soon.Model.Source.FavoriteCompanyDataLocalSource;
import com.lusle.android.soon.View.FavoriteCompanyList.FavoriteListActivity;
import com.lusle.android.soon.View.Main.Company.Presenter.CompanyContract;
import com.lusle.android.soon.View.Main.Company.Presenter.CompanyPresenter;
import com.lusle.android.soon.View.Dialog.MovieProgressDialog;
import com.lusle.android.soon.R;
import com.lusle.android.soon.Util.Util;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.skyfishjy.library.RippleBackground;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CompanyFragment extends Fragment implements CompanyContract.View {

    private TextView mMoreBtn, emptyText;
    private RecyclerView mFavorite;
    private RippleBackground rippleBackground;
    private CompanyPresenter presenter;
    private MovieProgressDialog dialog;

    public static CompanyFragment newInstance() {

        Bundle args = new Bundle();

        CompanyFragment fragment = new CompanyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_company, container, false);

        presenter = new CompanyPresenter();
        presenter.attachView(this);

        dialog = new MovieProgressDialog(getContext());

        emptyText = view.findViewById(R.id.fragment_company_empty_text);
        rippleBackground = view.findViewById(R.id.rippleBackground);
        mMoreBtn = view.findViewById(R.id.fragment_company_favorite_more);
        mFavorite = view.findViewById(R.id.fragment_company_favorite);

        rippleBackground.startRippleAnimation();

        mMoreBtn.setOnClickListener(v -> startActivity(new Intent(getContext(), FavoriteListActivity.class)));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        mFavorite.setLayoutManager(linearLayoutManager);
        FragmentCompanyFavoriteRecyclerViewAdapter adapter = new FragmentCompanyFavoriteRecyclerViewAdapter();
        presenter.setAdapterView(adapter);
        presenter.setAdapterModel(adapter);
        presenter.setModel(FavoriteCompanyDataLocalSource.getInstance());
        presenter.setOnItemClickListener();
        presenter.setOnEmptyListener();
        mFavorite.setAdapter(adapter);

        presenter.loadItems();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.loadItems();
    }

    @Override
    public void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }

    @Override
    public void showErrorToast() {
        DynamicToast.makeError(getContext(), "즐겨찾기 정보를 불러 올 수 없습니다.").show();
    }

    @Override
    public void showDialog(boolean show) {
        if (show) dialog.show();
        else dialog.dismiss();
    }

    @Override
    public void runRecyclerViewAnimation() {
        Util.runLayoutAnimation(mFavorite);
    }

    @Override
    public void setRecyclerEmpty(boolean visibility) {
        if (visibility) {
            emptyText.setVisibility(View.VISIBLE);
            rippleBackground.setVisibility(View.VISIBLE);
            mMoreBtn.setVisibility(View.GONE);
            rippleBackground.startRippleAnimation();
        } else {
            emptyText.setVisibility(View.GONE);
            rippleBackground.setVisibility(View.GONE);
            mMoreBtn.setVisibility(View.VISIBLE);
            rippleBackground.stopRippleAnimation();
        }
    }
}
