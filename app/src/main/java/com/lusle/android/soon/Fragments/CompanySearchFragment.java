package com.lusle.android.soon.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lusle.android.soon.API.APIClient;
import com.lusle.android.soon.API.APIInterface;
import com.lusle.android.soon.Adapter.BaseRecyclerAdapter;
import com.lusle.android.soon.Adapter.SearchActivityCompanyRecyclerViewAdapter;
import com.lusle.android.soon.Model.Company;
import com.lusle.android.soon.Model.CompanyResult;
import com.lusle.android.soon.MovieProgressDialog;
import com.lusle.android.soon.R;
import com.lusle.android.soon.Activity.SearchActivity;
import com.lusle.android.soon.Utils.Utils;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.lang.reflect.Type;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class CompanySearchFragment extends Fragment implements SearchActivity.OnQueryReceivedListener, SearchActivityCompanyRecyclerViewAdapter.OnClickFavoriteListener {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private SearchActivityCompanyRecyclerViewAdapter adapter;
    private ArrayList<Company> tempList;
    private FrameLayout emptyViewGroup;
    private LottieAnimationView emptyAnim;
    private String currentQuery = "";
    private Integer currentPage = 1;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        SharedPreferences pref = getActivity().getSharedPreferences("pref", MODE_PRIVATE);
        String list = pref.getString("favorite_company", "");
        Type type = new TypeToken<ArrayList<Company>>() {
        }.getType();
        ArrayList<Company> companyList = new Gson().fromJson(list, type);
        tempList = companyList;
        if (tempList == null) tempList = new ArrayList<>();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((SearchActivity) getActivity()).addQueryReceivedListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_company, container, false);
        recyclerView = view.findViewById(R.id.fragment_search_company_list);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SearchActivityCompanyRecyclerViewAdapter(recyclerView);
        adapter.setTempFavorite(tempList);
        adapter.setOnClickFavoriteListener(this);
        recyclerView.setAdapter(adapter);
        emptyViewGroup = view.findViewById(R.id.list_empty_view);
        emptyAnim = view.findViewById(R.id.list_empty_anim);
        adapter.setOnEmptyListener(new BaseRecyclerAdapter.OnEmptyListener() {
            @Override
            public void onEmpty() {
                emptyViewGroup.setVisibility(View.VISIBLE);
                emptyAnim.playAnimation();
            }

            @Override
            public void onNotEmpty() {
                emptyViewGroup.setVisibility(View.GONE);
                if (emptyAnim.isAnimating()) emptyAnim.pauseAnimation();
            }
        });
        adapter.setOnLoadMoreListener(() -> new Thread(() -> {
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            Call<CompanyResult> call = apiInterface.searchCompany(currentQuery, ++currentPage);
            call.enqueue(new Callback<CompanyResult>() {
                @Override
                public void onResponse(Call<CompanyResult> call, Response<CompanyResult> response) {
                    ArrayList<Company> list = response.body().getResults();
                    if (list.size() == 0) {
                        adapter.setLoaded();
                    } else {
                        adapter.addItems(list);
                        getActivity().runOnUiThread(() -> {
                            adapter.notifyDataSetChanged();
                            adapter.setLoaded();
                        });
                    }
                }

                @Override
                public void onFailure(Call<CompanyResult> call, Throwable t) {
                    adapter.onEmpty();
                    adapter.setLoaded();
                    DynamicToast.makeError(getContext(), getString(R.string.server_error_msg)).show();
                }
            });
        }).start());
        return view;
    }

    @Override
    public void onQueryReceived(String query) {
        adapter.clear();
        currentPage = 1;
        currentQuery = "";
        MovieProgressDialog dialog = new MovieProgressDialog(getContext());
        dialog.show();
        new Thread(() -> {
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            Call<CompanyResult> call = apiInterface.searchCompany(query, currentPage);
            call.enqueue(new Callback<CompanyResult>() {
                @Override
                public void onResponse(Call<CompanyResult> call, Response<CompanyResult> response) {
                    ArrayList<Company> list = response.body().getResults();
                    adapter.setItemLimit(response.body().getTotalResults());
                    adapter.setList(list);
                    getActivity().runOnUiThread(() -> {
                        Utils.runLayoutAnimation(recyclerView);
                        dialog.dismiss();
                        currentQuery = query;
                    });

                }

                @Override
                public void onFailure(Call<CompanyResult> call, Throwable t) {
                    adapter.onEmpty();
                    dialog.dismiss();
                    DynamicToast.makeError(getContext(), getString(R.string.server_error_msg)).show();
                    currentQuery = "";
                    currentPage = 1;
                }
            });
        }).start();
    }

    @Override
    public void OnClickFavorite(ArrayList<Company> listTobeSaved) {
        Type type = new TypeToken<ArrayList<Company>>() {
        }.getType();
        String list = new Gson().toJson(listTobeSaved, type);
        SharedPreferences pref = getActivity().getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("favorite_company", list);
        editor.commit();
    }
}
