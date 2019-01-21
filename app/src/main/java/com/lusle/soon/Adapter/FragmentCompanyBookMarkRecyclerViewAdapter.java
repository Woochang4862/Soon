package com.lusle.soon.Adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lusle.soon.R;

import java.util.ArrayList;

public class FragmentCompanyBookMarkRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<String> mList;
    private OnItemClickListener mItemClickListener;

    public FragmentCompanyBookMarkRecyclerViewAdapter() {
    }

    public FragmentCompanyBookMarkRecyclerViewAdapter(ArrayList<String> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.favorite_company_recyclerview_item, viewGroup, false);
        return new BookMarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((BookMarkViewHolder)viewHolder).textView.setText("#"+mList.get(i));
    }

    @Override
    public int getItemCount() {
        if(mList==null)
            return 0;
        return mList.size();
    }

    public class BookMarkViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        public TextView textView;

        public BookMarkViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            textView=itemView.findViewById(R.id.bookmark_recyclerview_item_company_name);
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

    public interface OnItemClickListener{

        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);

    }



    public void setList(ArrayList<String> list){
        mList=list;
    }

    public void setOnItemClickListener(final FragmentCompanyBookMarkRecyclerViewAdapter.OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public String getCompanyName(int position){
        return mList.get(position);
    }
}
