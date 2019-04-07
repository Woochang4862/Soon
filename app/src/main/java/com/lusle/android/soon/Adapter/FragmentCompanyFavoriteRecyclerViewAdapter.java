package com.lusle.android.soon.Adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lusle.android.soon.Model.Company;
import com.lusle.android.soon.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FragmentCompanyFavoriteRecyclerViewAdapter extends BaseRecyclerAdapter<RecyclerView.ViewHolder> {

    private ArrayList<Company> mList;
    private OnItemClickListener mItemClickListener;

    public FragmentCompanyFavoriteRecyclerViewAdapter() {
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_favorite_company_recyclerview, viewGroup, false);
        return new BookMarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((BookMarkViewHolder) viewHolder).textView.setText("#" + mList.get(i).getName());
        Picasso.get()
                .load("https://image.tmdb.org/t/p/w500"+mList.get(i).getLogo_path())
                .fit()
                .centerInside()
                .error(R.drawable.ic_broken_image)
                .into(((BookMarkViewHolder) viewHolder).imageView);
    }

    @Override
    public int getItemCount() {
        if (mList == null)
            return 0;
        return mList.size();
    }

    public Company getItem(int position) {
        return mList.get(position);
    }

    public class BookMarkViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView textView;
        public ImageView imageView;

        public BookMarkViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            textView = itemView.findViewById(R.id.favorite_recyclerview_item_company_name);
            imageView = itemView.findViewById(R.id.bookmark_recyclerview_item_poster);
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

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public void setList(ArrayList<Company> list) {
        mList = list;
    }

    public void setOnItemClickListener(final FragmentCompanyFavoriteRecyclerViewAdapter.OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}
