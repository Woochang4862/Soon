package com.lusle.soon;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.lusle.soon.Adapter.ContentResultGenreRecyclerViewAdapter;
import com.lusle.soon.Adapter.FragmentCompanyResultRecyclerViewAdapter;
import com.lusle.soon.Model.Genre;
import com.lusle.soon.Model.MovieDetail;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ContentResultGenreActivity extends BaseActivity {

    private static final int DATE = 333;
    private static final int POP = 909;
    private Genre genre;
    private int filterType = DATE;

    @BindView(R.id.activity_content_list_genre_keyword)
    TextView keyword;
    @BindView(R.id.activity_content_result_genre_expand_icon)
    ImageView expandIcon;
    @BindView(R.id.activity_content_list_genre_icon)
    ImageView icon;
    @BindView(R.id.activity_content_result_genre_date)
    TextView dateFilterText;
    @BindView(R.id.activity_content_result_genre_popular)
    TextView popFilterText;
    @BindView(R.id.activity_content_result_genre_recyclerview)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_result_genre);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        genre = (Genre) getIntent().getSerializableExtra("genre");

        keyword.setText(genre.getName());
        keyword.setOnClickListener(v->{
            finish();
        });
        expandIcon.setOnClickListener(v->{
            finish();
        });


        Picasso.get()
                .load(Integer.parseInt(genre.getIcon()))
                .centerInside()
                .resize(25,20)
                .onlyScaleDown()
                .into(icon);


        dateFilterText.setOnClickListener(v -> {
            if (filterType == POP) {
                dateFilterText.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                popFilterText.setTextColor(ContextCompat.getColor(this, R.color.defaultTextColor));
                filterType = DATE;
            }
        });
        popFilterText.setOnClickListener(v -> {
            if (filterType == DATE) {
                dateFilterText.setTextColor(ContextCompat.getColor(this, R.color.defaultTextColor));
                popFilterText.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                filterType = POP;
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(new ContentResultGenreRecyclerViewAdapter());
        setDataIntoRecyclerView((ContentResultGenreRecyclerViewAdapter) recyclerView.getAdapter());
    }

    private void setDataIntoRecyclerView(final ContentResultGenreRecyclerViewAdapter resultRecyclerViewAdapter) {
        new Thread(() -> {
            List<Genre> genres = new ArrayList<>();
            genres.add(new Genre(0, String.valueOf(R.drawable.ic_genre_action), "액션"));
            genres.add(new Genre(1, String.valueOf(R.drawable.ic_genre_animation), "애니메이션"));
            ArrayList<MovieDetail> tmp = new ArrayList<>();
            tmp.add(new MovieDetail("보헤미안 렙소디", "전체이용가", genres, "2020년 1월 1일"));
            tmp.add(new MovieDetail("b", "15세 이상 이용가", genres, "2020년 1월 1일"));
            tmp.add(new MovieDetail("c", "청소년 관람불가", genres, "2020년 1월 1일"));
            //TODO:LOAD DATA
            resultRecyclerViewAdapter.setList(tmp);
            runOnUiThread(() -> {
                resultRecyclerViewAdapter.notifyDataSetChanged();
                //TODO:ProgressBar
            });
        }).start();
    }
}
