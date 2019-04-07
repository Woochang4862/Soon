package com.lusle.android.soon.Adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lusle.android.soon.Model.Genre;
import com.lusle.android.soon.Model.Movie;
import com.lusle.android.soon.R;
import com.lusle.android.soon.Utils.Utils;
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

public class AllSearchActivityMovieRecyclerViewAdapter extends BaseRecyclerAdapter<RecyclerView.ViewHolder> {

    private ArrayList<Movie> list;
    private Map<Integer, String> genres;
    private OnItemClickListener onClickListener;
    private OnBookButtonClickListener onBookButtonClickListener;

    public AllSearchActivityMovieRecyclerViewAdapter() {
        list = new ArrayList<>();
        genres = new HashMap<>();
    }

    public class SearchMovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imageView;
        public TextView title, adult, genre, overview, release;
        public Button bookBtn;

        public SearchMovieViewHolder(@NonNull View itemView) {
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

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_search_recyclerview, parent, false);
        return new SearchMovieViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Picasso
                .get()
                .load("https://image.tmdb.org/t/p/w500" + list.get(position).getPosterPath())
                .centerCrop()
                .fit()
                .error(R.drawable.ic_broken_image)
                .into(((SearchMovieViewHolder) holder).imageView);


        ((SearchMovieViewHolder) holder).title.setText(list.get(position).getTitle());
        ((SearchMovieViewHolder) holder).adult.setVisibility(list.get(position).getAdult() ? View.VISIBLE : View.GONE);


        ArrayList<String> genreList = new ArrayList<>();
        for (Integer id : list.get(position).getGenreIds()) {
            genreList.add(this.genres.get(id));
        }
        ((SearchMovieViewHolder) holder).genre.setText(TextUtils.join(",", genreList));

        if (list.get(position).getOverview().equalsIgnoreCase(""))
            ((SearchMovieViewHolder) holder).overview.setVisibility(View.GONE);
        else if (list.get(position).getOverview().length() <= 40)
            ((SearchMovieViewHolder) holder).overview.setText(list.get(position).getOverview());
        else
            ((SearchMovieViewHolder) holder).overview.setText(list.get(position).getOverview().substring(0, 41));

        ((SearchMovieViewHolder) holder).release.setText("개봉일 : " + list.get(position).getReleaseDate());


        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(list.get(position).getReleaseDate());
            Calendar releaseDate = new GregorianCalendar();
            releaseDate.setTime(date);
            int day = Utils.calDDay(releaseDate);
            if (day <= 0) {
                ((SearchMovieViewHolder) holder).bookBtn.setEnabled(false);
                ((SearchMovieViewHolder) holder).bookBtn.setText("개봉함");

            } else {
                ((SearchMovieViewHolder) holder).bookBtn.setEnabled(true);
                ((SearchMovieViewHolder) holder).bookBtn.setText("DAY - " + day);
                ((SearchMovieViewHolder) holder).bookBtn.setOnClickListener(v -> {
                    if(onBookButtonClickListener != null)
                        onBookButtonClickListener.onBookButtonClicked(list.get(position));
                });
            }
        } catch (ParseException e) {
            e.printStackTrace();
            ((SearchMovieViewHolder) holder).bookBtn.setText("---");
            ((SearchMovieViewHolder) holder).bookBtn.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        if (list.size() == 0) return 0;
        return list.size();
    }

    public interface OnItemClickListener {
        void OnItemClick(View view, int position);
    }

    public void setList(ArrayList<Movie> list) {
        this.list = list;
    }

    public void setGenres(ArrayList<Genre> genres) {
        for (Genre genre : genres) {
            this.genres.put(genre.getId(), genre.getName());
        }
    }

    public void setOnClickListener(OnItemClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void clear() {
        if (list != null)
            list.clear();
    }

    public Movie getItem(int position){
        return list.get(position);
    }

    public void setOnBookButtonClickListener(OnBookButtonClickListener onBookButtonClickListener) {
        this.onBookButtonClickListener = onBookButtonClickListener;
    }
}
