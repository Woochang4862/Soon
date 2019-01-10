package com.lusle.soon;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lusle.soon.Adapter.SearchCompanyActivityCompanyRecyclerViewAdapter;
import com.lusle.soon.Model.Company;

import java.util.ArrayList;

public class SearchCompanyActivity extends BaseActivity {

    private SearchView mSearchView;
    private static TextView mTextViewBack, mTextViewForward;
    private static CheckBox mCheckBoxAll;
    private LinearLayout mLinearLayout;
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


        mSearchView = findViewById(R.id.activity_search_company_searchview);
        mSearchView.requestFocusFromTouch();
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return true;
            }
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //TODO:API from server
                ArrayList<Company> temp = new ArrayList<>();
                for (Company company : new Company[]{new Company(420, "/hUzeosd33nzE5MCNsZxCGEKTXaQ.png", "Marvel Studios"), new Company(19551, "/2WpWp9b108hizjHKdA107hFmvQ5.png", "Marvel Enterprises")}) {
                    temp.add(company);
                }
                activityCompanyRecyclerViewAdapter.addItems(temp);
                ItIsEmpty(activityCompanyRecyclerViewAdapter.getItemCount() <= 0);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });


        mCheckBoxAll = findViewById(R.id.activity_search_company_checkbox_all);
        mCheckBoxAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = ((CheckBox) v).isChecked();
                for (int i = 0; i < activityCompanyRecyclerViewAdapter.getItemCount(); i++) {
                    ((CheckBox) layoutManager.findViewByPosition(i).findViewById(R.id.company_recyclerview_checkbox)).setChecked(isChecked);
                }
            }
        });


        mTextViewBack = findViewById(R.id.activity_search_company_back_btn);
        mTextViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mTextViewForward = findViewById(R.id.activity_search_company_forward_btn);
        setDeactivation();
        mTextViewForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchCompanyActivity.this, MainActivity.class);
                intent.putExtra("company_list", activityCompanyRecyclerViewAdapter.getResult().toString()); //temporary
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void ItIsEmpty(boolean state) {
        mLinearLayout.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    public static void setDeactivation() {
        if (mTextViewForward != null) {
            mTextViewForward.setBackgroundColor(0x80FFFFFF);
            mTextViewForward.setEnabled(false);
        }
    }

    public static void setActivation() {
        if (mTextViewForward != null) {
            mTextViewForward.setBackgroundColor(0xFFFFFFFF);
            mTextViewForward.setEnabled(true);
        }
    }

    public static void setAllCheckBox(boolean state) {
        if (mCheckBoxAll != null) {
            mCheckBoxAll.setChecked(state);
        }
    }
}
