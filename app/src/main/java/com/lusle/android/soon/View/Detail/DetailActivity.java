package com.lusle.android.soon.View.Detail;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.lusle.android.soon.Util.Util;
import com.lusle.android.soon.View.BaseActivity;
import com.lusle.android.soon.View.YoutubePlayer.YoutubePlayerActivity;
import com.lusle.android.soon.Adapter.Listener.OnEmptyListener;
import com.lusle.android.soon.Model.API.APIClient;
import com.lusle.android.soon.Model.API.APIInterface;
import com.lusle.android.soon.Adapter.PreviewImageAdapter;
import com.lusle.android.soon.Adapter.VideoThumbnailAdapter;
import com.lusle.android.soon.Model.Schema.Backdrop;
import com.lusle.android.soon.Model.Schema.Genre;
import com.lusle.android.soon.Model.Schema.MovieDetail;
import com.lusle.android.soon.Model.Schema.Poster;
import com.lusle.android.soon.R;
import com.lusle.android.soon.Util.SubtitleCollapsingToolbarLayout.SubtitleCollapsingToolbarLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Response;

public class DetailActivity extends BaseActivity {

    private ImageView poster;
    private SubtitleCollapsingToolbarLayout toolbar;
    private RatingBar headerVoteAverage, voteAverage;
    private TextView voteCount, voteAverageLabel, voteAverageText, genre, releaseDate, runtime, popularity, overview, revenue, budget, originalLang, originalTitle, videoEmptyView, previewEmptyView;
    private RecyclerView videoRecyclerView, previewImageRecyclerView;
    private VideoThumbnailAdapter videoThumbnailAdapter;
    private PreviewImageAdapter previewImageAdapter;
    private LinearLayoutManager videoLayoutManager, previewImageLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        poster = findViewById(R.id.poster);
        toolbar = findViewById(R.id.collapsing_toolbar_layout);
        headerVoteAverage = findViewById(R.id.vote_average_header);

        voteCount = findViewById(R.id.vote_count);
        voteAverageLabel = findViewById(R.id.vote_label);
        voteAverage = findViewById(R.id.vote_average);
        voteAverageText = findViewById(R.id.vote_average_text);
        genre = findViewById(R.id.genre);
        releaseDate = findViewById(R.id.release_date);
        runtime = findViewById(R.id.runtime);
        popularity = findViewById(R.id.popularity);

        videoRecyclerView = findViewById(R.id.video_thumbnail_recyclerView);
        videoLayoutManager = new LinearLayoutManager(this);
        videoLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        videoRecyclerView.setLayoutManager(videoLayoutManager);

        videoThumbnailAdapter = new VideoThumbnailAdapter();
        videoThumbnailAdapter.setOnClickListener((view, position) -> {
            Intent intent = new Intent(this, YoutubePlayerActivity.class);
            intent.putExtra("VIDEO_ID", videoThumbnailAdapter.getItem(position).getKey());
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, ViewCompat.getTransitionName(view));
            startActivity(intent);
        });
        videoThumbnailAdapter.setOnEmptyListener(new OnEmptyListener() {
            @Override
            public void onEmpty() {
                videoEmptyView.setVisibility(View.VISIBLE);
                videoRecyclerView.setVisibility(View.GONE);
            }

            @Override
            public void onNotEmpty() {
                videoEmptyView.setVisibility(View.GONE);
                videoRecyclerView.setVisibility(View.VISIBLE);
            }
        });
        videoRecyclerView.setAdapter(videoThumbnailAdapter);

        overview = findViewById(R.id.overview);

        previewImageRecyclerView = findViewById(R.id.preview_image_recyclerView);
        previewImageLayoutManager = new LinearLayoutManager(this);
        previewImageLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        previewImageRecyclerView.setLayoutManager(previewImageLayoutManager);

        previewImageAdapter = new PreviewImageAdapter();
        previewImageAdapter.setOnItemClickListener((view, position) -> {
            //TODO:Go To Preview Image Activity
        });
        previewImageAdapter.setOnEmptyListener(new OnEmptyListener() {
            @Override
            public void onEmpty() {
                previewEmptyView.setVisibility(View.VISIBLE);
                previewImageRecyclerView.setVisibility(View.GONE);
            }

            @Override
            public void onNotEmpty() {
                previewEmptyView.setVisibility(View.GONE);
                previewImageRecyclerView.setVisibility(View.VISIBLE);
            }
        });
        previewImageRecyclerView.setAdapter(previewImageAdapter);

        revenue = findViewById(R.id.revenue);
        budget = findViewById(R.id.budget);
        originalLang = findViewById(R.id.original_lang);
        originalTitle = findViewById(R.id.original_title);

        videoEmptyView = findViewById(R.id.video_empty_view);
        previewEmptyView = findViewById(R.id.preview_image_empty_view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
        }

        Integer id = getIntent().getIntExtra("movie_id", -1);

        APIClient.getClient().create(APIInterface.class).getMovieDetails(id, Util.getRegionCode(this)).enqueue(new retrofit2.Callback<MovieDetail>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<MovieDetail> call, Response<MovieDetail> response) {
                MovieDetail data = response.body();

                Picasso
                        .get()
                        .load("https://image.tmdb.org/t/p/w500" + data.getPosterPath())
                        .into(poster, new Callback() {
                            @Override
                            public void onSuccess() {
                                scheduleStartPostponedImageViewTransition(poster);
                            }

                            @Override
                            public void onError(Exception e) {
                                onException();
                            }
                        });

                if (data.getVoteAverage().floatValue() / 2 == 0)
                    headerVoteAverage.setVisibility(View.GONE);
                else
                    headerVoteAverage.setRating(data.getVoteAverage().floatValue() / 2);

                toolbar.setTitle(data.getTitle());
                toolbar.setSubtitle(data.getTagline());

                if (data.getVoteCount() > 0) {
                    voteCount.setText(data.getVoteCount() + "");
                    voteAverageText.setText(data.getVoteAverage()+"/10");
                }
                else if (data.getVoteCount() == 0) {
                    voteCount.setVisibility(View.GONE);
                    voteAverageLabel.setText("평가되지 않음");
                    voteAverage.setVisibility(View.GONE);
                    voteAverageText.setVisibility(View.GONE);
                }
                voteAverage.setRating(data.getVoteAverage().floatValue());
                ArrayList<String> genreList = new ArrayList<>();
                for (Genre genre : data.getGenres())
                    genreList.add(genre.getName());
                genre.setText(TextUtils.join(",", genreList));
                releaseDate.setText(data.getReleaseDate());
                if (data.getRuntime() != null)
                    runtime.setText(data.getRuntime() + "분");
                else
                    runtime.setText("해당 정보 없음");

                popularity.setText(data.getPopularity()+"");

                if(data.getVideos().getResults().isEmpty()){
                    videoThumbnailAdapter.onEmpty();
                }else {
                    videoThumbnailAdapter.setList(data.getVideos().getResults());
                    videoThumbnailAdapter.notifyDataSetChanged();
                }

                if (data.getOverview().length() != 0)
                    overview.setText(data.getOverview());
                else
                    overview.setText("해당 정보 없음");

                if (data.getImages().getPosters().isEmpty() && data.getImages().getBackdrops().isEmpty()) {
                    previewImageAdapter.onEmpty();
                } else {
                    ArrayList<String> previewImages = new ArrayList<>();
                    for (Backdrop backdrop : data.getImages().getBackdrops())
                        previewImages.add(backdrop.getFilePath());
                    for (Poster poster : data.getImages().getPosters())
                        previewImages.add(poster.getFilePath());
                    previewImageAdapter.setList(previewImages);
                    previewImageAdapter.notifyDataSetChanged();
                }

                revenue.setText((data.getRevenue() == 0) ? "해당 정보 없음" : "$" + new DecimalFormat("###,###").format(data.getRevenue()));
                budget.setText((data.getBudget() == 0) ? "해당 정보 없음" : "$" + new DecimalFormat("###,###").format(data.getBudget()));
                originalLang.setText(data.getOriginalLanguage());
                originalTitle.setText(data.getOriginalTitle());
            }

            @Override
            public void onFailure(Call<MovieDetail> call, Throwable t) {
                onException();
            }
        });
    }

    private void onException() {
        scheduleStartPostponedImageViewTransition(poster);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    private void scheduleStartPostponedImageViewTransition(final ImageView imageView) {
        imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startPostponedEnterTransition();
                }
                return true;
            }
        });
    }
}
