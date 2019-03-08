package com.lusle.soon.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.android.material.appbar.AppBarLayout;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DateFragment extends Fragment {

    private AppBarLayout appBarLayout;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일", /*Locale.getDefault()*/Locale.KOREAN);
    private CompactCalendarView compactCalendarView;
    private TextView datePickerTextView;
    private boolean isExpanded = false;
    private FrameLayout emptyViewGroup;
    private LottieAnimationView emptyAnim;
    private RecyclerView mMovieList;
    private MovieListRecyclerViewAdapter adapter;
    private APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);

    private Date currDate = new Date();
    private Integer currPage = 1;

    private Activity activity;

    public static DateFragment newInstance() {

        Bundle args = new Bundle();

        DateFragment fragment = new DateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            activity = (Activity) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date, container, false);

        MovieProgressDialog dialog = new MovieProgressDialog(getContext());
        dialog.show();

        Toolbar toolbar = view.findViewById(R.id.fragment_date_toolbar);
        ((AppCompatActivity) getContext()).setSupportActionBar(toolbar);

        appBarLayout = view.findViewById(R.id.app_bar_layout);

        compactCalendarView = view.findViewById(R.id.compactcalendar_view);
        compactCalendarView.setLocale(TimeZone.getDefault(), Locale.getDefault());
        compactCalendarView.setShouldDrawDaysHeader(true);
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                setSubtitle(dateFormat.format(dateClicked));
                currDate = dateClicked;
                currPage = 1;
                bindingData();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                setSubtitle(dateFormat.format(firstDayOfNewMonth));
                currDate = firstDayOfNewMonth;
                currPage = 1;
                bindingData();
            }
        });

        datePickerTextView = view.findViewById(R.id.date_picker_text_view);
        setCurrentDate(new Date());
        final ImageView arrow = view.findViewById(R.id.date_picker_arrow);
        RelativeLayout datePickerButton = view.findViewById(R.id.date_picker_button);

        datePickerButton.setOnClickListener(v -> {
            float rotation = isExpanded ? 0 : 180;
            ViewCompat.animate(arrow).rotation(rotation).start();

            isExpanded = !isExpanded;
            appBarLayout.setExpanded(isExpanded, true);
        });

        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            ViewCompat.animate(arrow).rotation(((float) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange() * 180)).start();
            if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                isExpanded = false;
            } else {
                isExpanded = true;
            }
        });

        emptyViewGroup = view.findViewById(R.id.list_empty_view);
        emptyAnim = view.findViewById(R.id.list_empty_anim);

        mMovieList = view.findViewById(R.id.fragment_date_movie_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mMovieList.setLayoutManager(linearLayoutManager);
        adapter = new MovieListRecyclerViewAdapter(mMovieList);
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
                mMovieList.setVisibility(View.GONE);
                emptyViewGroup.setVisibility(View.VISIBLE);
                emptyAnim.playAnimation();
            }

            @Override
            public void onNotEmpty() {
                mMovieList.setVisibility(View.VISIBLE);
                emptyViewGroup.setVisibility(View.GONE);
                if (emptyAnim.isAnimating()) emptyAnim.pauseAnimation();
            }
        });
        adapter.setOnLoadMoreListener(() -> new Thread(() -> {
            currPage++;
            apiInterface.discoverMovieWithDate(new SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN).format(currDate), currPage).enqueue(new Callback<MovieResult>() {
                @Override
                public void onResponse(Call<MovieResult> call, Response<MovieResult> response) {
                    adapter.addItems(response.body().getResults());
                    adapter.notifyDataSetChanged();
                    adapter.setLoaded();
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


        //~~~~~~~
        apiInterface.getGenreList().enqueue(new Callback<GenreResult>() {
            @Override
            public void onResponse(Call<GenreResult> call, Response<GenreResult> response) {
                adapter.setGenres(response.body().getGenres());
            }

            @Override
            public void onFailure(Call<GenreResult> call, Throwable t) {
                adapter.onEmpty();
                DynamicToast.makeError(getContext(), getString(R.string.server_error_msg)).show();
            }
        });
        apiInterface.discoverMovieWithDate(new SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN).format(currDate), currPage).enqueue(new Callback<MovieResult>() {
            @Override
            public void onResponse(Call<MovieResult> call, Response<MovieResult> response) {
                adapter.setList(response.body().getResults());
                adapter.setItemLimit(response.body().getTotalResults());
                Utils.runLayoutAnimation(mMovieList);
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<MovieResult> call, Throwable t) {
                adapter.onEmpty();
                dialog.dismiss();
                DynamicToast.makeError(getContext(), getString(R.string.server_error_msg)).show();
            }
        });
        //~~~~~~~~~

        mMovieList.setAdapter(adapter);
        mMovieList.setFocusable(false);


        return view;
    }

    private void bindingData() {
        MovieProgressDialog dialog = new MovieProgressDialog(getContext());
        dialog.show();
        apiInterface.discoverMovieWithDate(new SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN).format(currDate), currPage).enqueue(new Callback<MovieResult>() {
            @Override
            public void onResponse(Call<MovieResult> call, Response<MovieResult> response) {
                adapter.setList(response.body().getResults());
                Utils.runLayoutAnimation(mMovieList);
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<MovieResult> call, Throwable t) {
                adapter.onEmpty();
                DynamicToast.makeError(getContext(), getString(R.string.server_error_msg)).show();
                dialog.dismiss();
            }
        });
    }

    private void setCurrentDate(Date date) {
        setSubtitle(dateFormat.format(date));
        if (compactCalendarView != null) {
            compactCalendarView.setCurrentDate(date);
        }
    }

    private void setSubtitle(String subtitle) {
        if (datePickerTextView != null) {
            datePickerTextView.setText(subtitle);
        }
    }
}
