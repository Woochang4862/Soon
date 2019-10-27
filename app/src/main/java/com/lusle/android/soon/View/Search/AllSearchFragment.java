package com.lusle.android.soon.View.Search;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lusle.android.soon.Adapter.Listener.OnEmptyListener;
import com.lusle.android.soon.Model.API.APIClient;
import com.lusle.android.soon.Model.API.APIInterface;
import com.lusle.android.soon.Adapter.AllSearchActivityCompanyRecyclerViewAdapter;
import com.lusle.android.soon.Adapter.AllSearchActivityMovieRecyclerViewAdapter;
import com.lusle.android.soon.View.Alarm.AlarmSettingActivity;
import com.lusle.android.soon.View.Detail.DetailActivity;
import com.lusle.android.soon.Model.Schema.Company;
import com.lusle.android.soon.Model.Schema.CompanyResult;
import com.lusle.android.soon.Model.Schema.GenreResult;
import com.lusle.android.soon.Model.Schema.Movie;
import com.lusle.android.soon.Model.Schema.MovieResult;
import com.lusle.android.soon.View.Dialog.MovieProgressDialog;
import com.lusle.android.soon.R;
import com.lusle.android.soon.Util.Util;

import java.lang.reflect.Type;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
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
    private LinearLayoutManager companyLayoutManager, movieLayoutManager;
    private RelativeLayout companyMoreBtn, movieMoreBtn;
    private boolean isCompanyExpanded = false, isMovieExpanded = false;
    private APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
    private ArrayList<Company> companyExpandArrayList, companyCollapseArrayList;
    private ArrayList<Movie> movieExpandArrayList, movieCollapseArrayList;
    private FrameLayout companyEmptyViewGroup;
    private LottieAnimationView companyEmptyAnim;
    private FrameLayout movieEmptyViewGroup;
    private LottieAnimationView movieEmptyAnim;

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
        companyResults = view.findViewById(R.id.company_results_more);
        movieResults = view.findViewById(R.id.movie_results_more);
        companyRecyclerView = view.findViewById(R.id.company_recyclerView);
        movieRecyclerView = view.findViewById(R.id.movie_recyclerView);
        companyMoreBtn = view.findViewById(R.id.company_more);
        movieMoreBtn = view.findViewById(R.id.movie_more);
        companyEmptyViewGroup = view.findViewById(R.id.list_empty_view_company);
        companyEmptyAnim = view.findViewById(R.id.list_empty_anim_company);
        movieEmptyViewGroup = view.findViewById(R.id.list_empty_view_movie);
        movieEmptyAnim = view.findViewById(R.id.list_empty_anim_movie);


        companyLayoutManager = new LinearLayoutManager(getContext());
        movieLayoutManager = new LinearLayoutManager(getContext());

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
                companyRecyclerView.setVisibility(View.GONE);
                companyEmptyViewGroup.setVisibility(View.VISIBLE);
                companyEmptyAnim.playAnimation();
            }

            @Override
            public void onNotEmpty() {
                companyRecyclerView.setVisibility(View.VISIBLE);
                companyEmptyViewGroup.setVisibility(View.GONE);
                if (companyEmptyAnim.isAnimating()) companyEmptyAnim.pauseAnimation();
            }
        });
        companyRecyclerView.setAdapter(companyAdapter);
        companyRecyclerView.setLayoutManager(companyLayoutManager);

        movieAdapter = new AllSearchActivityMovieRecyclerViewAdapter();
        movieAdapter.setOnEmptyListener(new OnEmptyListener() {
            @Override
            public void onEmpty() {
                movieRecyclerView.setVisibility(View.GONE);
                movieEmptyViewGroup.setVisibility(View.VISIBLE);
                movieEmptyAnim.playAnimation();
            }

            @Override
            public void onNotEmpty() {
                movieRecyclerView.setVisibility(View.VISIBLE);
                movieEmptyViewGroup.setVisibility(View.GONE);
                if (movieEmptyAnim.isAnimating()) movieEmptyAnim.pauseAnimation();
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
            Intent intent = new Intent(getContext(), AlarmSettingActivity.class);
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
                Util.runLayoutAnimation(companyRecyclerView);
                isCompanyExpanded = false;
            } else {
                ((TextView) companyMoreBtn.findViewById(R.id.company_more_text)).setText("접기");
                ((ImageView) companyMoreBtn.findViewById(R.id.company_more_image)).setImageDrawable(getResources().getDrawable(R.drawable.ic_expand_reverse));
                companyAdapter.setList(companyExpandArrayList);
                Util.runLayoutAnimation(companyRecyclerView);
                isCompanyExpanded = true;
            }
        });

        movieMoreBtn.setOnClickListener(v -> {
            if (isMovieExpanded) {
                ((TextView) movieMoreBtn.findViewById(R.id.movie_more_text)).setText("더 알아보기");
                ((ImageView) movieMoreBtn.findViewById(R.id.movie_more_image)).setImageDrawable(getResources().getDrawable(R.drawable.ic_expand_more));
                movieAdapter.setList(movieCollapseArrayList);
                Util.runLayoutAnimation(movieRecyclerView);
                isMovieExpanded = false;
            } else {
                ((TextView) movieMoreBtn.findViewById(R.id.movie_more_text)).setText("접기");
                ((ImageView) movieMoreBtn.findViewById(R.id.movie_more_image)).setImageDrawable(getResources().getDrawable(R.drawable.ic_expand_reverse));
                movieAdapter.setList(movieExpandArrayList);
                Util.runLayoutAnimation(movieRecyclerView);
                isMovieExpanded = true;
            }
        });

        return view;
    }

    @Override
    public void onQueryReceived(String query) {
        SharedPreferences pref = getActivity().getSharedPreferences("pref", MODE_PRIVATE);
        String list = pref.getString("favorite_company", "");
        Type type = new TypeToken<ArrayList<Company>>() {
        }.getType();
        ArrayList<Company> tempList = new Gson().fromJson(list, type);
        if (tempList == null) tempList = new ArrayList<>();
        companyAdapter.setTempFavorite(tempList);
        MovieProgressDialog dialog = new MovieProgressDialog(getContext());
        dialog.show();

        companyAdapter.clear();
        apiInterface.searchCompany(query, 1).enqueue(new Callback<CompanyResult>() {
            @Override
            public void onResponse(Call<CompanyResult> call, Response<CompanyResult> response) {
                CompanyResult result = response.body();
                if (result.getTotalResults() == 0) {
                    companySection.setVisibility(View.GONE);
                } else if (result.getTotalResults() != 0 && result.getTotalResults() <= 3) {
                    companySection.setVisibility(View.VISIBLE);
                    companyAdapter.setList(result.getResults());
                    companyResults.setText("총 " + result.getTotalResults() + "개의 결과 더보기");
                } else if (result.getTotalResults() != 0) {
                    companySection.setVisibility(View.VISIBLE);
                    companyExpandArrayList = result.getResults();
                    companyCollapseArrayList = new ArrayList<>();
                    for (int i = 0; i < 3; i++)
                        companyCollapseArrayList.add(result.getResults().get(i));
                    companyAdapter.setList(companyCollapseArrayList);
                    companyMoreBtn.setVisibility(View.VISIBLE);
                    companyResults.setText("총 " + result.getTotalResults() + "개의 결과 더보기");
                }
                companyAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<CompanyResult> call, Throwable t) {
                //TODO: Failure processing
                companyAdapter.onEmpty();
                dialog.dismiss();
            }
        });

        MovieProgressDialog dialog1 = new MovieProgressDialog(getContext());
        dialog1.show();
        apiInterface.getGenreList().enqueue(new Callback<GenreResult>() {
            @Override
            public void onResponse(Call<GenreResult> call, Response<GenreResult> response) {
                movieAdapter.setGenres(response.body().getGenres());
            }

            @Override
            public void onFailure(Call<GenreResult> call, Throwable t) {
                companyAdapter.onEmpty();
                dialog1.dismiss();
            }
        });
        movieAdapter.clear();
        apiInterface.searchMovie(query, 1).enqueue(new Callback<MovieResult>() {
            @Override
            public void onResponse(Call<MovieResult> call, Response<MovieResult> response) {
                MovieResult result = response.body();
                if (result.getTotalResults() == 0) {
                    movieSection.setVisibility(View.GONE);
                } else if (result.getTotalResults() != 0 && result.getTotalResults() <= 3) {
                    movieSection.setVisibility(View.VISIBLE);
                    movieAdapter.setList(result.getResults());
                    movieResults.setText("총 " + result.getTotalResults() + "개의 결과 더보기");
                } else if (result.getTotalResults() != 0) {
                    movieSection.setVisibility(View.VISIBLE);
                    movieExpandArrayList = result.getResults();
                    movieCollapseArrayList = new ArrayList<>();
                    for (int i = 0; i < 3; i++)
                        movieCollapseArrayList.add(result.getResults().get(i));
                    movieAdapter.setList(movieCollapseArrayList);
                    movieMoreBtn.setVisibility(View.VISIBLE);
                    movieResults.setText("총 " + result.getTotalResults() + "개의 결과 더보기");
                }
                movieAdapter.notifyDataSetChanged();
                dialog1.dismiss();
            }

            @Override
            public void onFailure(Call<MovieResult> call, Throwable t) {
                companyAdapter.onEmpty();
                dialog1.dismiss();
            }
        });
    }
}
