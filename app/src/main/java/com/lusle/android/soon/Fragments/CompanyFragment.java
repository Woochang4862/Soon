package com.lusle.android.soon.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lusle.android.soon.Adapter.BaseRecyclerAdapter;
import com.lusle.android.soon.Adapter.FragmentCompanyFavoriteRecyclerViewAdapter;
import com.lusle.android.soon.FavoriteListActivity;
import com.lusle.android.soon.MovieListActivity;
import com.lusle.android.soon.MovieProgressDialog;
import com.lusle.android.soon.R;
import com.lusle.android.soon.Utils.Utils;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.skyfishjy.library.RippleBackground;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CompanyFragment extends Fragment {

    private TextView mMoreBtn, emptyText;
    private RecyclerView mFavorite;
    private RippleBackground rippleBackground;

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

        MovieProgressDialog dialog = new MovieProgressDialog(getContext());
        dialog.show();

        emptyText = view.findViewById(R.id.fragment_company_empty_text);
        rippleBackground = view.findViewById(R.id.rippleBackground);
        rippleBackground.startRippleAnimation();

        mMoreBtn = view.findViewById(R.id.fragment_company_favorite_more);
        mMoreBtn.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), FavoriteListActivity.class));
        });
        mFavorite = view.findViewById(R.id.fragment_company_favorite);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        mFavorite.setLayoutManager(linearLayoutManager);
        final FragmentCompanyFavoriteRecyclerViewAdapter fragmentCompanyBookMarkRecyclerViewAdapter = new FragmentCompanyFavoriteRecyclerViewAdapter();
        fragmentCompanyBookMarkRecyclerViewAdapter.setOnItemClickListener(new FragmentCompanyFavoriteRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getContext(), MovieListActivity.class);
                intent.putExtra("keyword", fragmentCompanyBookMarkRecyclerViewAdapter.getItem(position));
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        fragmentCompanyBookMarkRecyclerViewAdapter.setOnEmptyListener(new BaseRecyclerAdapter.OnEmptyListener() {
            @Override
            public void onEmpty() {
                emptyText.setVisibility(View.VISIBLE);
                rippleBackground.setVisibility(View.VISIBLE);
                mMoreBtn.setVisibility(View.GONE);
                rippleBackground.startRippleAnimation();
            }

            @Override
            public void onNotEmpty() {
                emptyText.setVisibility(View.GONE);
                rippleBackground.setVisibility(View.GONE);
                mMoreBtn.setVisibility(View.VISIBLE);
                rippleBackground.stopRippleAnimation();
            }
        });
        mFavorite.setAdapter(fragmentCompanyBookMarkRecyclerViewAdapter);

        if (!Utils.bindingData(getContext(), mFavorite, "Favorite")) {
            DynamicToast.makeError(getContext(), "즐겨찾기 정보를 불러 올 수 없습니다.").show();
        }

        mFavorite.setFocusable(false);

        dialog.dismiss();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!Utils.bindingData(getContext(), mFavorite, "Favorite")) {
            DynamicToast.makeError(getContext(), "즐겨찾기 정보를 불러 올 수 없습니다.").show();
        }
    }
}
