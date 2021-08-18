package com.lusle.android.soon.View.Search;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lusle.android.soon.Adapter.Listener.OnEmptyListener;
import com.lusle.android.soon.Model.API.APIClient;
import com.lusle.android.soon.Model.API.APIInterface;
import com.lusle.android.soon.Adapter.AllSearchActivityCompanyRecyclerViewAdapter;
import com.lusle.android.soon.Adapter.AllSearchActivityMovieRecyclerViewAdapter;
import com.lusle.android.soon.Model.Schema.Movie;
import com.lusle.android.soon.Model.Schema.MovieResult;
import com.lusle.android.soon.View.Alarm.AlarmSettingFragment;
import com.lusle.android.soon.View.Detail.DetailActivity;
import com.lusle.android.soon.Model.Schema.Company;
import com.lusle.android.soon.Model.Schema.CompanyResult;
import com.lusle.android.soon.Model.Schema.GenreResult;
import com.lusle.android.soon.View.Dialog.MovieProgressDialog;
import com.lusle.android.soon.R;
import com.lusle.android.soon.Util.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class AllSearchFragment extends Fragment implements SearchActivity.OnQueryReceivedListener {

    private CardView companySection, movieSection;
    private TextView companyResults, movieResults;
    private RecyclerView companyRecyclerView, movieRecyclerView;
    private AllSearchActivityCompanyRecyclerViewAdapter companyAdapter;
    private AllSearchActivityMovieRecyclerViewAdapter movieAdapter;
    private RelativeLayout companyMoreBtn, movieMoreBtn;
    private boolean isCompanyExpanded = false, isMovieExpanded = false;
    private final APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
    private ArrayList<Company> companyExpandArrayList, companyCollapseArrayList;
    private ArrayList<Movie> movieExpandArrayList, movieCollapseArrayList;
    private View topSpace, bottomSpace, middleSpace;
    private MutableLiveData<Pair<Integer, Integer>> sectionVisibility;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((SearchActivity) getActivity()).addQueryReceivedListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_all, container, false);


        companySection = view.findViewById(R.id.company_section);
        movieSection = view.findViewById(R.id.movie_section);
        topSpace = view.findViewById(R.id.top_space);
        middleSpace = view.findViewById(R.id.middle_space);
        bottomSpace = view.findViewById(R.id.bottom_space);
        companyResults = view.findViewById(R.id.company_results_more);
        movieResults = view.findViewById(R.id.movie_results_more);
        companyRecyclerView = view.findViewById(R.id.company_recyclerView);
        movieRecyclerView = view.findViewById(R.id.movie_recyclerView);
        companyMoreBtn = view.findViewById(R.id.company_more);
        movieMoreBtn = view.findViewById(R.id.movie_more);

        companySection.setVisibility(View.GONE);
        movieSection.setVisibility(View.GONE);
        topSpace.setVisibility(View.GONE);
        middleSpace.setVisibility(View.GONE);
        bottomSpace.setVisibility(View.GONE);

        sectionVisibility = new MutableLiveData<>(new Pair<>(View.GONE, View.GONE));
        sectionVisibility.observe(getViewLifecycleOwner(), integerIntegerPair -> {
            topSpace.setVisibility(integerIntegerPair.first);
            middleSpace.setVisibility(integerIntegerPair.second);

            if(integerIntegerPair.first == View.GONE && integerIntegerPair.second == View.GONE){
                bottomSpace.setVisibility(View.GONE);
            } else {
                bottomSpace.setVisibility(View.VISIBLE);
            }
        });

        LinearLayoutManager companyLayoutManager = new LinearLayoutManager(getContext());
        LinearLayoutManager movieLayoutManager = new LinearLayoutManager(getContext());

        companyAdapter = new AllSearchActivityCompanyRecyclerViewAdapter();
        companyAdapter.setOnClickFavoriteListener(listTobeSaved -> {
            Type type = new TypeToken<ArrayList<Company>>() {
            }.getType();
            String list = new Gson().toJson(listTobeSaved, type);
            SharedPreferences pref = getActivity().getSharedPreferences("pref", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("favorite_company", list);
            editor.apply();
        });
        companyAdapter.setOnEmptyListener(new OnEmptyListener() {
            @Override
            public void onEmpty() {
                companySection.setVisibility(View.GONE);
                sectionVisibility.postValue(getPairOfVisibilityOfSection());
                companyMoreBtn.setVisibility(View.GONE);
                companyRecyclerView.setVisibility(View.GONE);
            }

            @Override
            public void onNotEmpty() {
                companyRecyclerView.setVisibility(View.VISIBLE);
                companySection.setVisibility(View.VISIBLE);
                sectionVisibility.postValue(getPairOfVisibilityOfSection());
            }
        });
        companyRecyclerView.setAdapter(companyAdapter);
        companyRecyclerView.setLayoutManager(companyLayoutManager);

        movieAdapter = new AllSearchActivityMovieRecyclerViewAdapter();
        movieAdapter.setOnEmptyListener(new OnEmptyListener() {
            @Override
            public void onEmpty() {
                movieRecyclerView.setVisibility(View.GONE);
                movieSection.setVisibility(View.GONE);
                sectionVisibility.postValue(getPairOfVisibilityOfSection());
                movieMoreBtn.setVisibility(View.GONE);
            }

            @Override
            public void onNotEmpty() {
                movieRecyclerView.setVisibility(View.VISIBLE);
                movieSection.setVisibility(View.VISIBLE);
                sectionVisibility.postValue(getPairOfVisibilityOfSection());
            }
        });
        movieAdapter.setOnClickListener((v, i) -> {
            Intent intent = new Intent(getContext(), DetailActivity.class);
            intent.putExtra("movie_id", movieAdapter.getItem(i).getId());
            Pair<View, String> poster = Pair.create(v.findViewById(R.id.movie_list_recyclerview_poster), ViewCompat.getTransitionName(v.findViewById(R.id.movie_list_recyclerview_poster)));
            Pair<View, String> title = Pair.create(v.findViewById(R.id.movie_list_recyclerView_title), ViewCompat.getTransitionName(v.findViewById(R.id.movie_list_recyclerView_title)));
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), poster, title);
            startActivity(intent, options.toBundle());
        });
        movieAdapter.setOnBookButtonClickListener(movie -> {
            Intent intent = new Intent(getContext(), AlarmSettingFragment.class);
            intent.putExtra("movie_info", movie);
            startActivity(intent);
        });
        movieRecyclerView.setAdapter(movieAdapter);
        movieRecyclerView.setLayoutManager(movieLayoutManager);


        companyResults.setOnClickListener(v -> ((SearchActivity) getActivity()).getViewPager().setCurrentItem(1));

        movieResults.setOnClickListener(v -> ((SearchActivity) getActivity()).getViewPager().setCurrentItem(2));


        companyMoreBtn.setOnClickListener(v -> {
            if (isCompanyExpanded) {
                ((TextView) companyMoreBtn.findViewById(R.id.company_more_text)).setText("더 알아보기");
                ((ImageView) companyMoreBtn.findViewById(R.id.company_more_image)).setImageDrawable(getResources().getDrawable(R.drawable.ic_expand_more));
                companyAdapter.setList(companyCollapseArrayList);
                Utils.runLayoutAnimation(companyRecyclerView);
                isCompanyExpanded = false;
            } else {
                ((TextView) companyMoreBtn.findViewById(R.id.company_more_text)).setText("접기");
                ((ImageView) companyMoreBtn.findViewById(R.id.company_more_image)).setImageDrawable(getResources().getDrawable(R.drawable.ic_expand_reverse));
                companyAdapter.setList(companyExpandArrayList);
                Utils.runLayoutAnimation(companyRecyclerView);
                isCompanyExpanded = true;
            }
        });

        movieMoreBtn.setOnClickListener(v -> {
            if (isMovieExpanded) {
                ((TextView) movieMoreBtn.findViewById(R.id.movie_more_text)).setText("더 알아보기");
                ((ImageView) movieMoreBtn.findViewById(R.id.movie_more_image)).setImageDrawable(getResources().getDrawable(R.drawable.ic_expand_more));
                movieAdapter.setList(movieCollapseArrayList);
                Utils.runLayoutAnimation(movieRecyclerView);
                isMovieExpanded = false;
            } else {
                ((TextView) movieMoreBtn.findViewById(R.id.movie_more_text)).setText("접기");
                ((ImageView) movieMoreBtn.findViewById(R.id.movie_more_image)).setImageDrawable(getResources().getDrawable(R.drawable.ic_expand_reverse));
                movieAdapter.setList(movieExpandArrayList);
                Utils.runLayoutAnimation(movieRecyclerView);
                isMovieExpanded = true;
            }
        });

        return view;
    }

    private Pair<Integer, Integer> getPairOfVisibilityOfSection() {
        return new Pair<>(companySection.getVisibility(), movieSection.getVisibility());
    }

    @Override
    public void onQueryReceived(String query) {
        SharedPreferences pref = getActivity().getSharedPreferences("pref", MODE_PRIVATE);
        String list = pref.getString("favorite_company", "");
        Type type = new TypeToken<ArrayList<Company>>() {
        }.getType();
        ArrayList<Company> favoriteCompanyList = new Gson().fromJson(list, type);
        if (favoriteCompanyList == null) favoriteCompanyList = new ArrayList<>();
        companyAdapter.setTempFavorite(favoriteCompanyList);

        companyAdapter.clear();
        movieAdapter.clear();

        MovieProgressDialog dialog = new MovieProgressDialog(getContext());
        dialog.show();
        apiInterface.searchCompany(query, Utils.getRegionCode(getContext()), 1).enqueue(new Callback<CompanyResult>() {
            @Override
            public void onResponse(@NonNull Call<CompanyResult> call, @NonNull Response<CompanyResult> response) {
                CompanyResult result = response.body();
                if (result.getTotalResults() == 0) {
                    companyAdapter.onEmpty();
                } else {
                    companyAdapter.onNotEmpty();
                    if (result.getTotalResults() <= 3) {
                        companyAdapter.setList(result.getResults());
                    } else {
                        companyExpandArrayList = result.getResults();
                        companyCollapseArrayList = new ArrayList<>(result.getResults().subList(0, 4));
                        companyAdapter.setList(companyCollapseArrayList);
                        companyMoreBtn.setVisibility(View.VISIBLE);
                    }
                    companyResults.setText("총 " + result.getTotalResults() + "개의 결과 더보기");
                }
                companyAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<CompanyResult> call, Throwable t) {
                companyAdapter.onEmpty();
                dialog.dismiss();
            }
        });

        dialog.show();
        apiInterface.getGenreList().enqueue(new Callback<GenreResult>() {
            @Override
            public void onResponse(@NonNull Call<GenreResult> call, @NonNull Response<GenreResult> response) {
                movieAdapter.setGenres(response.body().getGenres());
                apiInterface.searchMovie(query, Utils.getRegionCode(getContext()), 1).enqueue(new Callback<MovieResult>() {
                    @Override
                    public void onResponse(@NonNull Call<MovieResult> call, @NonNull Response<MovieResult> response) {
                        MovieResult result = response.body();
                        if (result.getTotalResults() == 0) {
                            movieAdapter.onEmpty();
                        } else {
                            movieAdapter.onNotEmpty();
                            if (result.getTotalResults() <= 3) {
                                movieAdapter.setList(result.getResults());
                            } else {
                                movieExpandArrayList = result.getResults();
                                movieCollapseArrayList = new ArrayList<>(result.getResults().subList(0, 4));
                                movieAdapter.setList(movieCollapseArrayList);
                                movieMoreBtn.setVisibility(View.VISIBLE);
                            }
                            movieResults.setText("총 " + result.getTotalResults() + "개의 결과 더보기");
                            movieAdapter.notifyDataSetChanged();
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(@NonNull Call<MovieResult> call, @NonNull Throwable t) {
                        companyAdapter.onEmpty();
                        dialog.dismiss();
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<GenreResult> call, @NonNull Throwable t) {
                companyAdapter.onEmpty();
                dialog.dismiss();
            }
        });

    }
}
