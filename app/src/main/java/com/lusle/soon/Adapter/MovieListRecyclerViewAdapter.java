package com.lusle.soon.Adapter;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lusle.soon.Model.Genre;
import com.lusle.soon.Model.Movie;
import com.lusle.soon.R;
import com.lusle.soon.Utils.Utils;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MovieListRecyclerViewAdapter extends BaseRecyclerAdapter<RecyclerView.ViewHolder> {

    private ArrayList<Movie> mList;
    private Map<Integer, String> genres;
    private OnClickListener onClickListener;
    private OnBookButtonClickListener onBookButtonClickListener;

    public MovieListRecyclerViewAdapter(RecyclerView recyclerView) {
        super(recyclerView);
        genres = new HashMap<>();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imageView;

        public TextView title, adult, genre, overview, release;
        public Button bookBtn;
        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imageView = itemView.findViewById(R.id.movie_list_recyclerview_poster);
            title = itemView.findViewById(R.id.movie_list_recyclerView_title);
            adult = itemView.findViewById(R.id.movie_list_recyclerview_adult);
            genre = itemView.findViewById(R.id.movie_list_recyclerview_genre);
            overview = itemView.findViewById(R.id.movie_list_recyclerview_overview);
            release = itemView.findViewById(R.id.movie_list_recyclerview_release);
            bookBtn = itemView.findViewById(R.id.movie_list_recyclerview_d_day);
        }

        @Override
        public void onClick(View v) {
            if (onClickListener != null) {
                onClickListener.OnItemClick(v, getLayoutPosition());
            }
        }


    }
    @Override
    public int getItemViewType(int position) {
        return (position < mList.size()) ? VIEW_ITEM : VIEW_PROG;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_PROG) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar_recyclerview_load_more, parent, false);
            vh = new ProgressViewHolder(v);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_recyclerview, parent, false);
            vh = new MovieViewHolder(view);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MovieViewHolder) {
            Picasso
                    .get()
                    .load("https://image.tmdb.org/t/p/w500" + mList.get(position).getPosterPath())
                    .centerCrop()
                    .fit()
                    .error(R.drawable.ic_broken_image)
                    .into(((MovieViewHolder) holder).imageView);


            ((MovieViewHolder) holder).title.setText(mList.get(position).getTitle());
            ((MovieViewHolder) holder).adult.setVisibility(mList.get(position).getAdult() ? View.VISIBLE : View.GONE);


            ArrayList<String> genreList = new ArrayList<>();
            for (Integer id : mList.get(position).getGenreIds()) {
                genreList.add(this.genres.get(id));
            }
            ((MovieViewHolder) holder).genre.setText(TextUtils.join(",", genreList));

            if (mList.get(position).getOverview().length() <= 40)
                ((MovieViewHolder) holder).overview.setText(mList.get(position).getOverview());
            else
                ((MovieViewHolder) holder).overview.setText(mList.get(position).getOverview().substring(0, 41));
            Log.d("DateFragment", ((MovieViewHolder) holder).overview.getText().toString());

            ((MovieViewHolder) holder).release.setText("개봉일 : " + mList.get(position).getReleaseDate());


            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = sdf.parse(mList.get(position).getReleaseDate());
                Calendar releaseDate = new GregorianCalendar();
                releaseDate.setTime(date);
                int day = Utils.calDDay(releaseDate);
                if (day <= 0) {
                    ((MovieViewHolder) holder).bookBtn.setEnabled(false);
                    ((MovieViewHolder) holder).bookBtn.setText("개봉함");
                } else {
                    ((MovieViewHolder) holder).bookBtn.setEnabled(true);
                    ((MovieViewHolder) holder).bookBtn.setText("DAY - " + day);
                    ((MovieViewHolder) holder).bookBtn.setOnClickListener(v -> {
                        if(onBookButtonClickListener != null)
                            onBookButtonClickListener.onBookButtonClicked(mList.get(position));
                    });
                }
            } catch (ParseException e) {
                e.printStackTrace();
                ((MovieViewHolder) holder).bookBtn.setText("---");
                ((MovieViewHolder) holder).bookBtn.setEnabled(false);
            }
        } else {
            if (limit == position) {
                ((ProgressViewHolder) holder).progressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mList == null) return 0;
        return mList.size() + 1;
    }

    public interface OnClickListener {

        void OnItemClick(View v, int pos);

    }
    public void setList(ArrayList<Movie> list) {
        mList = list;
    }

    public void setGenres(ArrayList<Genre> genres) {
        for (Genre genre : genres) {
            this.genres.put(genre.getId(), genre.getName());
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void addItems(ArrayList<Movie> list) {
        this.mList.addAll(list);
    }

    public Movie getItem(int i){
        return mList.get(i);
    }

    public boolean hasGenre() {
        return !genres.isEmpty();
    }


    public void setOnBookButtonClickListener(OnBookButtonClickListener onBookButtonClickListener) {
        this.onBookButtonClickListener = onBookButtonClickListener;
    }
}
