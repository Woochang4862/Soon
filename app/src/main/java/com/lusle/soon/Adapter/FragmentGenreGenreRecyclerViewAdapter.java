package com.lusle.soon.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lusle.soon.Model.Genre;
import com.lusle.soon.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FragmentGenreGenreRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Genre> mList;
    private OnItemClickListener onItemClickListener;
    private String baseUrl = "";

    private class GenreViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView genreIcon;
        public TextView genreText;

        public GenreViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            genreIcon = itemView.findViewById(R.id.genre_icon);
            genreText = itemView.findViewById(R.id.genre_text);
        }

        @Override
        public void onClick(View v) {
            if(onItemClickListener!=null){
                onItemClickListener.OnItemClick(v, getLayoutPosition());
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.genre_recyclerview_item, parent, false);
        return new GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //TODO:Binding View
        Log.d("Genre","onBindViewHolder");
        /*Picasso.get()
                .load(baseUrl+mList.get(position).getIcon())
                .centerCrop()
                .fit()
                .into(((GenreViewHolder)holder).genreIcon);*/
        ((GenreViewHolder)holder).genreIcon.setImageResource(Integer.parseInt(mList.get(position).getIcon()));
        ((GenreViewHolder)holder).genreText.setText(mList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        if(mList==null) return 0;
        return mList.size();
    }

    public interface OnItemClickListener{
        void OnItemClick(View view, int position);
    }

    public void SetOnClickLister(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public void setmList(ArrayList<Genre> mList){
        this.mList = mList;
    }

    public void addData(Genre item){
        mList.add(item);
    }

    public void addDatas(ArrayList<Genre> items){
        mList.addAll(items);
    }

    public Genre getItem(int position){
        return mList.get(position);
    }
}
