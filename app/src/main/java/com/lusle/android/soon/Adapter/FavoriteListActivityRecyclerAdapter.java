package com.lusle.android.soon.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lusle.android.soon.Activity.FavoriteListActivity;
import com.lusle.android.soon.ItemTouchHelper.ItemTouchHelperAdapter;
import com.lusle.android.soon.ItemTouchHelper.ItemTouchHelperViewHolder;
import com.lusle.android.soon.Model.Company;
import com.lusle.android.soon.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;

public class FavoriteListActivityRecyclerAdapter extends BaseRecyclerAdapter<FavoriteListActivityRecyclerAdapter.FavoriteCell> implements ItemTouchHelperAdapter {

    private ArrayList<Company> mList;

    private final OnDragStartListener mDragStartListener;

    public FavoriteListActivityRecyclerAdapter(OnDragStartListener mDragStartListener) {
        this.mDragStartListener = mDragStartListener;
    }

    public class FavoriteCell extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        public TextView companyName;

        public ImageView handle;

        public FavoriteCell(@NonNull View itemView) {
            super(itemView);
            companyName = itemView.findViewById(R.id.companyName);
            handle = itemView.findViewById(R.id.handle);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }


    }
    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Company prev = mList.remove(fromPosition);
        mList.add(toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
        FavoriteListActivity.checkSaveBtn();
    }

    @Override
    public void onItemDismiss(int position) {
        Company deletedItem = mList.remove(position);
        notifyItemRemoved(position);
        FavoriteListActivity.ShowUndoSnackBar(deletedItem, position);
        FavoriteListActivity.checkSaveBtn();
    }

    @NonNull
    @Override
    public FavoriteCell onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_recyclerview, parent, false);
        return new FavoriteCell(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteCell holder, int position) {
        holder.companyName.setText(mList.get(position).getName());
        holder.handle.setOnTouchListener((view, event) -> {
            if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                mDragStartListener.onDragStarted(holder);
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        if (mList == null)
            return 0;
        return mList.size();
    }

    public void setList(ArrayList<Company> list) {
        this.mList = list;
    }

    public void insertItem(Company item, int i) {
        mList.add(i, item);
        notifyItemInserted(i);
        FavoriteListActivity.checkSaveBtn();
    }

    public ArrayList<Company> getList() {
        return mList;
    }

    public interface OnDragStartListener {

        void onDragStarted(RecyclerView.ViewHolder viewHolder);

    }

}
