package com.lusle.soon.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.lusle.soon.API.APIClient;
import com.lusle.soon.API.APIInterface;
import com.lusle.soon.Adapter.BaseRecyclerAdapter;
import com.lusle.soon.Adapter.SearchActivityMovieRecyclerViewAdapter;
import com.lusle.soon.AlarmSettingActivity;
import com.lusle.soon.DetailActivity;
import com.lusle.soon.Model.GenreResult;
import com.lusle.soon.Model.Movie;
import com.lusle.soon.Model.MovieResult;
import com.lusle.soon.MovieProgressDialog;
import com.lusle.soon.R;
import com.lusle.soon.SearchActivity;
import com.lusle.soon.Utils.Utils;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieSearchFragment extends Fragment implements SearchActivity.OnQueryReceivedListener {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private SearchActivityMovieRecyclerViewAdapter adapter;
    private FrameLayout emptyViewGroup;
    private LottieAnimationView emptyAnim;
    private String currentQuery = "";
    private Integer currentPage = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((SearchActivity) getActivity()).addQueryReceivedListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_movie, container, false);
        recyclerView = view.findViewById(R.id.movieRecyclerView);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SearchActivityMovieRecyclerViewAdapter(recyclerView);
        adapter.setOnClickListener((view1, position) -> {
            Intent intent = new Intent(getContext(), DetailActivity.class);
            intent.putExtra("movie_id", adapter.getItem(position).getId());
            Pair<View, String> poster = Pair.create(view1.findViewById(R.id.movie_list_recyclerview_poster), ViewCompat.getTransitionName(view1.findViewById(R.id.movie_list_recyclerview_poster)));
            Pair<View, String> title = Pair.create(view1.findViewById(R.id.movie_list_recyclerView_title), ViewCompat.getTransitionName(view1.findViewById(R.id.movie_list_recyclerView_title)));
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), poster, title);
            startActivity(intent, options.toBundle());
        });

        recyclerView.setAdapter(adapter);
        emptyViewGroup = view.findViewById(R.id.list_empty_view);
        emptyAnim = view.findViewById(R.id.list_empty_anim);
        adapter.setOnEmptyListener(new BaseRecyclerAdapter.OnEmptyListener() {
            @Override
            public void onEmpty() {
                recyclerView.setVisibility(View.GONE);
                emptyViewGroup.setVisibility(View.VISIBLE);
                emptyAnim.playAnimation();
            }

            @Override
            public void onNotEmpty() {
                recyclerView.setVisibility(View.VISIBLE);
                emptyViewGroup.setVisibility(View.GONE);
                if (emptyAnim.isAnimating()) emptyAnim.pauseAnimation();
            }
        });
        adapter.setOnLoadMoreListener(() -> new Thread(() -> {
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            Call<MovieResult> call = apiInterface.searchMovie(currentQuery, ++currentPage);
            call.enqueue(new Callback<MovieResult>() {
                @Override
                public void onResponse(Call<MovieResult> call, Response<MovieResult> response) {
                    ArrayList<Movie> list = response.body().getResults();
                    if (list.size() != 0) {
                        adapter.addItems(list);
                        getActivity().runOnUiThread(() -> {
                            adapter.notifyDataSetChanged();
                            adapter.setLoaded();
                        });
                    } else {
                        adapter.setLoaded();
                    }
                }

                @Override
                public void onFailure(Call<MovieResult> call, Throwable t) {
                    adapter.onEmpty();
                    adapter.setLoaded();
                    DynamicToast.makeError(getContext(), getString(R.string.server_error_msg)).show();
                }
            });
        }).start());
        adapter.setOnBookButtonClickListener(movie->{
            Intent intent = new Intent(getContext(), AlarmSettingActivity.class);
            intent.putExtra("movie_info", movie);
            startActivity(intent);
        });
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
            apiInterface.getGenreList().enqueue(new Callback<GenreResult>() {
                @Override
                public void onResponse(Call<GenreResult> call, Response<GenreResult> response) {
                    adapter.setGenres(response.body().getGenres());
                }

                @Override
                public void onFailure(Call<GenreResult> call, Throwable t) {
                    adapter.onEmpty();
                    dialog.dismiss();
                    DynamicToast.makeError(getContext(), getString(R.string.server_error_msg)).show();
                    currentQuery = "";
                    currentPage = 1;
                    return;
                }
            });
            Call<MovieResult> call = apiInterface.searchMovie(query, currentPage);
            call.enqueue(new Callback<MovieResult>() {
                @Override
                public void onResponse(Call<MovieResult> call, Response<MovieResult> response) {
                    ArrayList<Movie> list = response.body().getResults();
                    adapter.setItemLimit(response.body().getTotalResults());
                    adapter.setList(list);
                    getActivity().runOnUiThread(() -> {
                        Utils.runLayoutAnimation(recyclerView);
                        dialog.dismiss();
                        currentQuery = query;
                    });
                }

                @Override
                public void onFailure(Call<MovieResult> call, Throwable t) {
                    Log.d(call.request().url().toString()+":", t.getMessage());
                    adapter.onEmpty();
                    dialog.dismiss();
                    DynamicToast.makeError(getContext(), getString(R.string.server_error_msg)).show();
                    currentQuery = "";
                    currentPage = 1;
                }
            });
        }).start();
    }
}