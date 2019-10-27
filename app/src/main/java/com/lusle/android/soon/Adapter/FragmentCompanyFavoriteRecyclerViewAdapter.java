package com.lusle.android.soon.Adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lusle.android.soon.Adapter.Contract.FragmentFavoriteCompanyAdapterContract;
import com.lusle.android.soon.Adapter.Holder.BookMarkViewHolder;
import com.lusle.android.soon.Adapter.Listener.OnItemClickListener;
import com.lusle.android.soon.Model.Schema.Company;
import com.lusle.android.soon.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FragmentCompanyFavoriteRecyclerViewAdapter extends BaseRecyclerAdapter<RecyclerView.ViewHolder> implements FragmentFavoriteCompanyAdapterContract.View, FragmentFavoriteCompanyAdapterContract.Model {

    private ArrayList<Company> mList;
    private OnItemClickListener onItemClickListener;

    public FragmentCompanyFavoriteRecyclerViewAdapter() {
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_favorite_company_recyclerview, viewGroup, false);
        return new BookMarkViewHolder(view, onItemClickListener);
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

    @Override
    public Company getItem(int position) {
        return mList.get(position);
    }

    @Override
    public void setList(ArrayList<Company> list) {
        mList = list;
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
