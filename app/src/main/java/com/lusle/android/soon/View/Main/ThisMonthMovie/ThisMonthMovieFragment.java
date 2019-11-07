package com.lusle.android.soon.View.Main.ThisMonthMovie;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.lusle.android.soon.Adapter.MovieListRecyclerViewAdapter;
import com.lusle.android.soon.Model.Source.GenreDataRemoteSource;
import com.lusle.android.soon.Model.Source.MovieDataRemoteSource;
import com.lusle.android.soon.View.Detail.DetailActivity;
import com.lusle.android.soon.View.Dialog.MovieProgressDialog;
import com.lusle.android.soon.R;
import com.lusle.android.soon.Util.Util;
import com.lusle.android.soon.View.Main.ThisMonthMovie.Presenter.ThisMonthMovieContract;
import com.lusle.android.soon.View.Main.ThisMonthMovie.Presenter.ThisMonthMoviePresenter;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ThisMonthMovieFragment extends Fragment implements ThisMonthMovieContract.View {

    private FrameLayout emptyView;
    private LottieAnimationView emptyAnim;
    private RecyclerView recyclerView;
    private MovieListRecyclerViewAdapter adapter;
    private LinearLayoutManager layoutManager;
    private MovieProgressDialog dialog;

    private ThisMonthMovieContract.Presenter presenter;

    public static ThisMonthMovieFragment newInstance() {

        Bundle args = new Bundle();

        ThisMonthMovieFragment fragment = new ThisMonthMovieFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_this_month_movie, container, false);

        presenter = new ThisMonthMoviePresenter();
        presenter.attachView(this);
        presenter.setMovieModel(MovieDataRemoteSource.getInstance());
        presenter.setGenreModel(GenreDataRemoteSource.getInstance());

        dialog = new MovieProgressDialog(getContext());

        emptyView = view.findViewById(R.id.list_empty_view);
        emptyAnim = view.findViewById(R.id.list_empty_anim);
        recyclerView = view.findViewById(R.id.movie_list_recyclerView);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MovieListRecyclerViewAdapter(recyclerView);
        presenter.setMovieAdapterModel(adapter);
        presenter.setMovieAdapterView(adapter);
        recyclerView.setAdapter(adapter);

        presenter.setOnItemClickListener((v, pos) -> {
            Intent intent = new Intent(getContext(), DetailActivity.class);
            intent.putExtra("movie_id", presenter.getItem(pos).getId());
            Pair<View, String> poster = Pair.create(v.findViewById(R.id.movie_list_recyclerview_poster), ViewCompat.getTransitionName(v.findViewById(R.id.movie_list_recyclerview_poster)));
            Pair<View, String> title = Pair.create(v.findViewById(R.id.movie_list_recyclerView_title), ViewCompat.getTransitionName(v.findViewById(R.id.movie_list_recyclerView_title)));
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) view.getContext(), poster, title);
            startActivity(intent, options.toBundle());
        });
        presenter.setOnEmptyListener();
        presenter.setOnLoadMoreListener();
        presenter.setOnBookButtonClickListener();
        presenter.loadItems(1, true);

        return view;
    }

    @Override
    public void showErrorToast() {
        DynamicToast.makeError(getContext(), getString(R.string.server_error_msg)).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public void showDialog(boolean show) {
        if (dialog.isShowing() != show) {
            if ((show)) {
                dialog.dismiss();
            } else {
                dialog.show();
            }
        }
    }

    @Override
    public void runRecyclerViewAnimation() {
        Util.runLayoutAnimation(recyclerView);
    }

    @Nullable
    @Override
    public Context getContext() {
        return super.getContext();
    }

    @Override
    public void setRecyclerEmpty(boolean empty) {
        if (empty) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            emptyAnim.setVisibility(View.VISIBLE);
            if (!emptyAnim.isAnimating()) emptyAnim.playAnimation();
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            emptyAnim.setVisibility(View.GONE);
            if (emptyAnim.isAnimating()) emptyAnim.pauseAnimation();
        }
    }
}
