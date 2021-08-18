package com.lusle.android.soon.View.Main.Date;

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
import com.lusle.android.soon.Adapter.Listener.OnEmptyListener;
import com.lusle.android.soon.Adapter.Listener.OnItemClickListener;
import com.lusle.android.soon.Adapter.MoviePagedListAdapter;
import com.lusle.android.soon.Model.Source.GenreDataRemoteSource;
import com.lusle.android.soon.Model.Source.MovieDataRemoteSource;
import com.lusle.android.soon.R;
import com.lusle.android.soon.Util.Utils;
import com.lusle.android.soon.View.Detail.DetailActivity;
import com.lusle.android.soon.View.Dialog.MovieProgressDialog;
import com.lusle.android.soon.View.Main.Date.Presenter.DateContract;
import com.lusle.android.soon.View.Main.Date.Presenter.DatePresenter;
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

public class DateFragment extends Fragment implements DateContract.View {

    private AppBarLayout appBarLayout;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일", /*Locale.getDefault()*/Locale.KOREAN);
    private CompactCalendarView compactCalendarView;
    private TextView datePickerTextView;
    private boolean isExpanded = false;
    private FrameLayout emptyViewGroup;
    private LottieAnimationView emptyAnim;
    private RecyclerView mMovieList;
    private MoviePagedListAdapter adapter;

    private Date currDate = new Date();

    private DatePresenter presenter;
    private MovieProgressDialog dialog;
    private RelativeLayout datePickerButton;
    private ImageView arrow;
    private LinearLayoutManager linearLayoutManager;

    public static DateFragment newInstance() {

        Bundle args = new Bundle();

        DateFragment fragment = new DateFragment();
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
        View view = inflater.inflate(R.layout.fragment_date, container, false);

        presenter = new DatePresenter();
        presenter.attachView(this);
        presenter.setGenreModel(GenreDataRemoteSource.getInstance());
        presenter.setMovieModel(MovieDataRemoteSource.getInstance());

        dialog = new MovieProgressDialog(getContext());

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
                presenter.loadItems(new SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN).format(currDate), true);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                setSubtitle(dateFormat.format(firstDayOfNewMonth));
                currDate = firstDayOfNewMonth;
                presenter.loadItems(new SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN).format(currDate), true);
            }
        });

        datePickerTextView = view.findViewById(R.id.date_picker_text_view);
        setCurrentDate(new Date());
        arrow = view.findViewById(R.id.date_picker_arrow);
        datePickerButton = view.findViewById(R.id.date_picker_button);
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
        linearLayoutManager = new LinearLayoutManager(getContext());
        mMovieList.setLayoutManager(linearLayoutManager);
        adapter = new MoviePagedListAdapter(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(view.getContext(), DetailActivity.class);
                intent.putExtra("movie_id", adapter.getItem(position).getId());
                Pair<View, String> poster = Pair.create(view.findViewById(R.id.movie_list_recyclerview_poster), ViewCompat.getTransitionName(view.findViewById(R.id.movie_list_recyclerview_poster)));
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) view.getContext(), poster);
                view.getContext().startActivity(intent, options.toBundle());
            }
        }, new OnEmptyListener() {
            @Override
            public void onEmpty() {

            }

            @Override
            public void onNotEmpty() {

            }
        });
        //presenter.setAdapterView(adapter);
        //presenter.setAdapterModel(adapter);
        mMovieList.setAdapter(adapter);
        presenter.setOnItemClickListener();
        presenter.setOnEmptyListener();
        presenter.setOnLoadMoreListener(new SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN).format(currDate));
        presenter.setOnBookButtonClickListener();

        presenter.loadItems(new SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN).format(currDate), true);

        mMovieList.setFocusable(false);

        return view;
    }

    @Override
    public void showDialog(boolean show) {
        if (show) {
            dialog.show();
        } else {
            dialog.dismiss();
        }
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

    @Override
    public void showErrorToast() {
        DynamicToast.makeError(getContext(), getString(R.string.server_error_msg)).show();
    }

    @Override
    public void runRecyclerViewAnimation() {
        Utils.runLayoutAnimation(mMovieList);
    }

    @Override
    public void setRecyclerEmpty(boolean isEmpty) {
        if (isEmpty) {
            mMovieList.setVisibility(View.GONE);
            emptyViewGroup.setVisibility(View.VISIBLE);
            emptyAnim.playAnimation();
        } else {
            mMovieList.setVisibility(View.VISIBLE);
            emptyViewGroup.setVisibility(View.GONE);
            if (emptyAnim.isAnimating()) emptyAnim.pauseAnimation();
        }
    }

    @Override
    public void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }
}
