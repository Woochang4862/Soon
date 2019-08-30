package com.lusle.android.soon.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.hugocastelani.waterfalltoolbar.WaterfallToolbar;
import com.lusle.android.soon.API.APIClient;
import com.lusle.android.soon.API.APIInterface;
import com.lusle.android.soon.Adapter.BaseRecyclerAdapter;
import com.lusle.android.soon.Adapter.MovieListRecyclerViewAdapter;
import com.lusle.android.soon.Model.Company;
import com.lusle.android.soon.Model.Genre;
import com.lusle.android.soon.Model.GenreResult;
import com.lusle.android.soon.Model.MovieResult;
import com.lusle.android.soon.MovieProgressDialog;
import com.lusle.android.soon.R;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.lusle.android.soon.Utils.Utils.runLayoutAnimation;

public class MovieListActivity extends BaseActivity {

    private Genre genre;
    private Company company;

    @BindView(R.id.waterfallToolbar)
    WaterfallToolbar toolbar;
    @BindView(R.id.activity_movie_list_keyword)
    TextView keyword;
    @BindView(R.id.activity_movie_list_expand_icon)
    ImageView expandIcon;
    @BindView(R.id.activity_movie_list_recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.list_empty_view)
    FrameLayout emptyViewGroup;
    @BindView(R.id.list_empty_anim)
    LottieAnimationView emptyAnim;
    private MovieListRecyclerViewAdapter adapter;
    private Integer currPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moive_list);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        toolbar.setRecyclerView(recyclerView);

        Object obj = getIntent().getSerializableExtra("keyword");
        if (obj instanceof Genre) {
            genre = (Genre) obj;
            keyword.setText(genre.getName());
        } else if (obj instanceof Company) {
            company = (Company) obj;
            keyword.setText(company.getName());
        }
        keyword.setOnClickListener(v -> finish());
        expandIcon.setOnClickListener(v -> finish());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MovieListRecyclerViewAdapter(recyclerView);
        adapter.setOnClickListener((v, pos) -> {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("movie_id", adapter.getItem(pos).getId());
            Pair<View, String> poster = Pair.create(v.findViewById(R.id.movie_list_recyclerview_poster), ViewCompat.getTransitionName(v.findViewById(R.id.movie_list_recyclerview_poster)));
            Pair<View, String> title = Pair.create(v.findViewById(R.id.movie_list_recyclerView_title), ViewCompat.getTransitionName(v.findViewById(R.id.movie_list_recyclerView_title)));
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, poster, title);
            startActivity(intent, options.toBundle());
        });
        adapter.setOnBookButtonClickListener(movie -> {
            Intent intent = new Intent(this, AlarmSettingActivity.class);
            intent.putExtra("movie_info", movie);
            startActivity(intent);
        });
        adapter.setOnEmptyListener(new BaseRecyclerAdapter.OnEmptyListener() {
            @Override
            public void onEmpty() {
                recyclerView.setVisibility(View.GONE);
                emptyViewGroup.setVisibility(View.VISIBLE);
                if (!emptyAnim.isAnimating()) emptyAnim.playAnimation();
            }

            @Override
            public void onNotEmpty() {
                recyclerView.setVisibility(View.VISIBLE);
                emptyViewGroup.setVisibility(View.GONE);
                if (emptyAnim.isAnimating()) emptyAnim.pauseAnimation();
            }
        });
        recyclerView.setAdapter(adapter);

        MovieProgressDialog dialog = new MovieProgressDialog(this);
        dialog.show();
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
                DynamicToast.makeError(MovieListActivity.this, getString(R.string.server_error_msg)).show();
            }
        });
        if (genre != null) {
            Call<MovieResult> call = apiInterface.discoverMovieWithGenre(genre.getId(), currPage);
            call.enqueue(new Callback<MovieResult>() {
                @Override
                public void onResponse(Call<MovieResult> call, Response<MovieResult> response) {
                    adapter.setItemLimit(response.body().getTotalResults());
                    adapter.setList(response.body().getResults());
                    runLayoutAnimation(recyclerView);
                    dialog.dismiss();
                }

                @Override
                public void onFailure(Call<MovieResult> call, Throwable t) {
                    adapter.onEmpty();
                    dialog.dismiss();
                    DynamicToast.makeError(MovieListActivity.this, getString(R.string.server_error_msg)).show();
                }
            });
            adapter.setOnLoadMoreListener(() -> new Thread(() -> {
                currPage++;
                apiInterface.discoverMovieWithGenre(genre.getId(), currPage).enqueue(new Callback<MovieResult>() {
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
                        DynamicToast.makeError(MovieListActivity.this, getString(R.string.server_error_msg)).show();
                    }
                });
            }).start());
        } else {
            Call<MovieResult> call = apiInterface.discoverMovieWithCompany(company.getId(), currPage);
            call.enqueue(new Callback<MovieResult>() {
                @Override
                public void onResponse(Call<MovieResult> call, Response<MovieResult> response) {
                    adapter.setItemLimit(response.body().getTotalResults());
                    adapter.setList(response.body().getResults());
                    runLayoutAnimation(recyclerView);
                    dialog.dismiss();
                }

                @Override
                public void onFailure(Call<MovieResult> call, Throwable t) {
                    adapter.onEmpty();
                    dialog.dismiss();
                    DynamicToast.makeError(MovieListActivity.this, getString(R.string.server_error_msg)).show();
                }
            });
            adapter.setOnLoadMoreListener(() -> new Thread(() -> {
                currPage++;
                apiInterface.discoverMovieWithCompany(company.getId(), currPage).enqueue(new Callback<MovieResult>() {
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
                        DynamicToast.makeError(MovieListActivity.this, getString(R.string.server_error_msg)).show();
                    }
                });
            }).start());
        }
    }
}
