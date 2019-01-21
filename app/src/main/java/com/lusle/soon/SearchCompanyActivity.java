package com.lusle.soon;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.lusle.soon.Adapter.SearchCompanyActivityCompanyRecyclerViewAdapter;
import com.lusle.soon.Model.Company;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.ArrayList;

public class SearchCompanyActivity extends BaseActivity {

    private SearchView mSearchView;
    private ImageView mBackBtn, mSearchBtn;
    private static TextView mTextViewForward;
    private static CheckBox mCheckBoxAll;
    private LinearLayout mLinearLayout;
    private LottieAnimationView empty;
    private RecyclerView mRecyclerView;
    private ArrayList<String> companyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_company);

        init();
    }

    private void init() {

        companyList = new ArrayList<>();

        mRecyclerView = findViewById(R.id.activity_search_company_company_list);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        mRecyclerView.setLayoutManager(layoutManager);
        final SearchCompanyActivityCompanyRecyclerViewAdapter activityCompanyRecyclerViewAdapter = new SearchCompanyActivityCompanyRecyclerViewAdapter();
        mRecyclerView.setAdapter(activityCompanyRecyclerViewAdapter);


        mLinearLayout = findViewById(R.id.activity_search_company_empty_img);
        empty = findViewById(R.id.activity_search_company_empty);
        empty.playAnimation();
        empty.loop(true);


        mSearchView = findViewById(R.id.activity_search_company_searchview);
        mSearchView.requestFocusFromTouch();
        mSearchView.setOnCloseListener(() -> true);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                activityCompanyRecyclerViewAdapter.clear();
                setDateIntoResult(activityCompanyRecyclerViewAdapter, s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });


        mCheckBoxAll = findViewById(R.id.activity_search_company_checkbox_all);
        mCheckBoxAll.setOnClickListener(v -> {
            boolean isChecked = ((CheckBox) v).isChecked();
            for (int i = 0; i < activityCompanyRecyclerViewAdapter.getItemCount(); i++) {
                ((CheckBox) layoutManager.findViewByPosition(i).findViewById(R.id.company_recyclerview_checkbox)).setChecked(isChecked);
            }
        });


        mBackBtn = findViewById(R.id.activity_search_company_back_btn);
        mBackBtn.setOnClickListener(v -> finish());


        mSearchBtn = findViewById(R.id.activity_search_company_search_btn);
        mSearchBtn.setOnClickListener(v -> {
            if (mSearchView.getQuery().toString().equals("")) {
                DynamicToast.makeWarning(this, "검색어를 넣어 주세요").show();
            } else {
                mSearchView.setQuery(mSearchView.getQuery(), true);
            }
        });

        mTextViewForward = findViewById(R.id.activity_search_company_forward_btn);
        setDeactivation();
        mTextViewForward.setOnClickListener(v -> {
            Intent intent = new Intent(SearchCompanyActivity.this, MainActivity.class);
            intent.putExtra("company_list", activityCompanyRecyclerViewAdapter.getResult());
            setResult(RESULT_OK, intent);
            finish();
        });

        ItIsEmpty(true);
    }

    private void setDateIntoResult(SearchCompanyActivityCompanyRecyclerViewAdapter adapter, String query) {
        new Thread(()->{
            ArrayList<Company> temp = new ArrayList<>();
            for (Company company : new Company[]{
                    new Company(420, "/hUzeosd33nzE5MCNsZxCGEKTXaQ.png", "Marvel Studios"),
                    new Company(19551, "/2WpWp9b108hizjHKdA107hFmvQ5.png", "Marvel Enterprises"),
                    new Company(38679, "/7sD79XoadVfcgOVCjuEgQduob68.png", "Marvel Television"),
                    new Company(2301, null, "Marvel Productions"),
                    new Company(13252, "/fR0wNyLTP6cexkfwSf49J24dlES.png", "Marvel Animation"),
                    new Company(108634, null, "Marvel Films"),
                    new Company(7505, "/837VMM4wOkODc1idNxGT0KQJlej.png", "Marvel Entertainment"),
                    new Company(11106, "/h4XR8uTNylVX9hJTiN50e76booZ.png", "Marvel Knights")
            }) {
                temp.add(company);
            }//TODO:API from server
            adapter.addItems(temp);
            runOnUiThread(()->{
                ItIsEmpty(adapter.getItemCount() <= 0);
                DynamicToast.makeSuccess(getApplicationContext(), mSearchView.getQuery() + "에 대한 검색 결과입니다").show();
            });
        }).start();
    }

    private void ItIsEmpty(boolean state) {
        mLinearLayout.setVisibility(state ? View.VISIBLE : View.GONE);
        if (state) empty.playAnimation();
        empty.loop(state);

        mCheckBoxAll.setVisibility(state ? View.GONE : View.VISIBLE);
        mRecyclerView.setVisibility(state ? View.GONE : View.VISIBLE);
    }

    public static void setDeactivation() {
        if (mTextViewForward != null) {
            mTextViewForward.setBackgroundColor(0x80f95a70);
            mTextViewForward.setEnabled(false);
        }
    }

    public static void setActivation() {
        if (mTextViewForward != null) {
            mTextViewForward.setBackgroundColor(0xfff95a70);
            mTextViewForward.setEnabled(true);
        }
    }

    public static void setAllCheckBox(boolean state) {
        if (mCheckBoxAll != null) {
            mCheckBoxAll.setChecked(state);
        }
    }
}
