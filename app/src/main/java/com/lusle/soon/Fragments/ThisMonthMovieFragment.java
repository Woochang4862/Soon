package com.lusle.soon.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.lusle.soon.API.APIClient;
import com.lusle.soon.API.APIInterface;
import com.lusle.soon.Adapter.BaseRecyclerAdapter;
import com.lusle.soon.Adapter.MovieListRecyclerViewAdapter;
import com.lusle.soon.AlarmSettingActivity;
import com.lusle.soon.DetailActivity;
import com.lusle.soon.Model.GenreResult;
import com.lusle.soon.Model.MovieResult;
import com.lusle.soon.MovieProgressDialog;
import com.lusle.soon.R;
import com.lusle.soon.Utils.Utils;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

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

public class ThisMonthMovieFragment extends Fragment {

    private FrameLayout emptyView;
    private LottieAnimationView emptyAnim;
    private RecyclerView recyclerView;
    private MovieListRecyclerViewAdapter adapter;
    private LinearLayoutManager layoutManager;
    private Activity activity;

    private int page = 1;

    public static ThisMonthMovieFragment newInstance() {

        Bundle args = new Bundle();

        ThisMonthMovieFragment fragment = new ThisMonthMovieFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof Activity)
            activity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_this_month_movie, container, false);

        MovieProgressDialog dialog = new MovieProgressDialog(getContext());

        emptyView = view.findViewById(R.id.list_empty_view);
        emptyAnim = view.findViewById(R.id.list_empty_anim);

        recyclerView = view.findViewById(R.id.movie_list_recyclerView);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MovieListRecyclerViewAdapter(recyclerView);
        adapter.setOnClickListener((v, pos) -> {
            Intent intent = new Intent(getContext(), DetailActivity.class);
            intent.putExtra("movie_id", adapter.getItem(pos).getId());
            Pair<View, String> poster = Pair.create(v.findViewById(R.id.movie_list_recyclerview_poster), ViewCompat.getTransitionName(v.findViewById(R.id.movie_list_recyclerview_poster)));
            Pair<View, String> title = Pair.create(v.findViewById(R.id.movie_list_recyclerView_title), ViewCompat.getTransitionName(v.findViewById(R.id.movie_list_recyclerView_title)));
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, poster, title);
            startActivity(intent, options.toBundle());
        });
        adapter.setOnEmptyListener(new BaseRecyclerAdapter.OnEmptyListener() {
            @Override
            public void onEmpty() {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                emptyAnim.setVisibility(View.VISIBLE);
                if (!emptyAnim.isAnimating()) emptyAnim.playAnimation();
            }

            @Override
            public void onNotEmpty() {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                emptyAnim.setVisibility(View.GONE);
                if (emptyAnim.isAnimating()) emptyAnim.pauseAnimation();
            }
        });
        adapter.setOnLoadMoreListener(() -> APIClient.getClient().create(APIInterface.class).getThisMonthMovie(++page).enqueue(new Callback<MovieResult>() {
            @Override
            public void onResponse(Call<MovieResult> call, Response<MovieResult> response) {
                adapter.setItemLimit(response.body().getTotalResults());
                adapter.addItems(response.body().getResults());
                adapter.notifyDataSetChanged();
                adapter.setLoaded();
            }

            @Override
            public void onFailure(Call<MovieResult> call, Throwable t) {
                adapter.onEmpty();
                DynamicToast.makeError(getContext(), getString(R.string.server_error_msg)).show();
            }
        }));
        recyclerView.setAdapter(adapter);

        APIClient.getClient().create(APIInterface.class).getGenreList().enqueue(new Callback<GenreResult>() {
            @Override
            public void onResponse(Call<GenreResult> call, Response<GenreResult> response) {
                adapter.setGenres(response.body().getGenres());
                APIClient.getClient().create(APIInterface.class).getThisMonthMovie(page).enqueue(new Callback<MovieResult>() {
                    @Override
                    public void onResponse(Call<MovieResult> call, Response<MovieResult> response) {
                        adapter.setItemLimit(response.body().getTotalResults());
                        adapter.setList(response.body().getResults());
                        Utils.runLayoutAnimation(recyclerView);
                        adapter.onNotEmpty();
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<MovieResult> call, Throwable t) {
                        adapter.onEmpty();
                        DynamicToast.makeError(getContext(), getString(R.string.server_error_msg)).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<GenreResult> call, Throwable t) {
                adapter.onEmpty();
                DynamicToast.makeError(getContext(), getString(R.string.server_error_msg)).show();
            }
        });
        adapter.setOnBookButtonClickListener(movie->{
            Intent intent = new Intent(getContext(), AlarmSettingActivity.class);
            intent.putExtra("movie_info", movie);
            startActivity(intent);
        });

        return view;
    }
}
