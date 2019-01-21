package com.lusle.soon.Adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lusle.soon.Model.MovieDetail;
import com.lusle.soon.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FragmentCompanyResultRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<MovieDetail> mList;
    private FragmentCompanyResultRecyclerViewAdapter.OnItemClickListener mItemClickListener;

    public FragmentCompanyResultRecyclerViewAdapter(){
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.company_result_recyclerview_item, viewGroup, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Picasso
                .get()
                .load("http://img.movist.com/?img=/x00/05/05/99_p1.jpg")
                .centerCrop()
                .fit()
                .into(((ResultViewHolder) viewHolder).imageView);
        String text = mList.get(i).getTitle() + "\n"
                +"평점: "+mList.get(i).getVoteAverage() + "\n"
                +mList.get(i).getCertification();
        Spannable spannable = new SpannableString(text);

        spannable.setSpan(
                new StyleSpan(android.graphics.Typeface.BOLD),
                text.indexOf(mList.get(i).getTitle()),
                text.indexOf(mList.get(i).getTitle())+mList.get(i).getTitle().length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        ((ResultViewHolder) viewHolder).textView.setText(spannable, TextView.BufferType.SPANNABLE);

        //TODO:Book Button Listener
    }

    @Override
    public int getItemCount() {
        if (mList == null)
            return 0;
        return mList.size();
    }

    private class ResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ImageView imageView;
        public TextView textView;
        public Button bookBtn;

        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            imageView = itemView.findViewById(R.id.result_recyclerview_poster);
            textView = itemView.findViewById(R.id.result_recyclerview_summary);
            bookBtn = itemView.findViewById(R.id.result_recyclerview_book_btn);
        }

        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(view, getLayoutPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemLongClick(view, getLayoutPosition());
                return true;
            }
            return false;
        }
    }

    public void clear() {
        mList.clear();
        notifyDataSetChanged();
    }

    public void setList(ArrayList<MovieDetail> mList){
        this.mList = mList;
    }

    public void addDatas(ArrayList<MovieDetail> list){
        mList.addAll(list);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(final FragmentCompanyResultRecyclerViewAdapter.OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}