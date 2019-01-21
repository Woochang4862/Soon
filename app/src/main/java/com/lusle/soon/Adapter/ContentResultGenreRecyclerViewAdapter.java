package com.lusle.soon.Adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lusle.soon.Model.Genre;
import com.lusle.soon.Model.MovieDetail;
import com.lusle.soon.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ContentResultGenreRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<MovieDetail> mList;
    private OnClickListener onClickListener;

    public void setList(ArrayList<MovieDetail> list) {
        mList=list;
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imageView;
        public TextView textView;
        public Button bookBtn;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imageView = itemView.findViewById(R.id.result_recyclerview_poster);
            textView = itemView.findViewById(R.id.result_recyclerview_summary);
            bookBtn = itemView.findViewById(R.id.result_recyclerview_book_btn);
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
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.company_result_recyclerview_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Picasso
                .get()
                .load("http://img.movist.com/?img=/x00/05/05/99_p1.jpg")
                .centerCrop()
                .fit()
                .into(((MovieViewHolder) holder).imageView);
        String text = mList.get(position).getTitle() + "\n"
                + mList.get(position).getCertification() + "\n"
                + "개봉일 : " + mList.get(position).getReleaseDate() + "\n"
                + "장르 : ";
        for (Genre genre : mList.get(position).getGenres()) {
            text += genre.getName() + ", ";
        }
        if (text != null && text.length() > 0) {
            text = text.substring(0, text.length() - 2);
        }
        Spannable spannable = new SpannableString(text);

        spannable.setSpan(
                new StyleSpan(android.graphics.Typeface.BOLD),
                text.indexOf(mList.get(position).getTitle()),
                text.indexOf(mList.get(position).getTitle()) + mList.get(position).getTitle().length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        ((MovieViewHolder) holder).textView.setText(spannable, TextView.BufferType.SPANNABLE);
    }

    @Override
    public int getItemCount() {
        if (mList == null) return 0;
        return mList.size();
    }

    public interface OnClickListener {
        void OnItemClick(View v, int pos);
    }

    public void SetOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
