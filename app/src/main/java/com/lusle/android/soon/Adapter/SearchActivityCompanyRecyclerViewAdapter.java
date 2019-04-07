package com.lusle.android.soon.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.lusle.android.soon.Model.Company;
import com.lusle.android.soon.R;
import com.lusle.android.soon.Utils.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SearchActivityCompanyRecyclerViewAdapter extends BaseRecyclerAdapter<RecyclerView.ViewHolder> {
    private ArrayList<Company> mList;
    private ArrayList<Company> tempFavorite;
    private OnClickFavoriteListener onClickFavoriteListener;

    public SearchActivityCompanyRecyclerViewAdapter() {
        mList = new ArrayList<>();
        tempFavorite = new ArrayList<>();
    }

    public SearchActivityCompanyRecyclerViewAdapter(RecyclerView recyclerView) {
        super(recyclerView);
        mList = new ArrayList<>();
        tempFavorite = new ArrayList<>();
    }

    public class CompanyViewHolder extends RecyclerView.ViewHolder {

        public ImageView logo;

        public TextView textView;
        public LottieAnimationView mLottieAnimationView;
        public CompanyViewHolder(@NonNull View itemView) {
            super(itemView);
            logo = itemView.findViewById(R.id.logo);
            textView = itemView.findViewById(R.id.company_recyclerView_textView);
            mLottieAnimationView = itemView.findViewById(R.id.company_recyclerview_bookmark);
        }

    }

    @Override
    public int getItemViewType(int position) {
        return (position < mList.size()) ? VIEW_ITEM : VIEW_PROG;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_PROG) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.progressbar_recyclerview_load_more, viewGroup, false);
            vh = new ProgressViewHolder(v);
        } else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_compay_recyclerview, viewGroup, false);
            vh = new CompanyViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {
        if (viewHolder instanceof CompanyViewHolder) {
            Picasso
                    .get()
                    .load("https://image.tmdb.org/t/p/w500" + mList.get(i).getLogo_path())
                    .centerCrop()
                    .transform(new CircleTransform())
                    .fit()
                    .error(R.drawable.ic_broken_image)
                    .into(((CompanyViewHolder) viewHolder).logo);

            ((CompanyViewHolder) viewHolder).textView.setText(mList.get(i).getName());

            if (tempFavorite.contains(mList.get(i)))
                ((CompanyViewHolder) viewHolder).mLottieAnimationView.setProgress(1);
            else
                ((CompanyViewHolder) viewHolder).mLottieAnimationView.setProgress(0);

            ((CompanyViewHolder) viewHolder).mLottieAnimationView.setOnClickListener(v -> {
                if (!((CompanyViewHolder) viewHolder).mLottieAnimationView.isAnimating()) {
                    if (tempFavorite.contains(mList.get(i))) { //삭제
                        tempFavorite.remove(mList.get(i));
                        ((LottieAnimationView) v).setSpeed(-2);
                        ((LottieAnimationView) v).playAnimation();
                        onClickFavoriteListener.OnClickFavorite(tempFavorite);
                    } else { //추가
                        tempFavorite.add(mList.get(i));
                        ((LottieAnimationView) v).setSpeed(2);
                        ((LottieAnimationView) v).playAnimation();
                        onClickFavoriteListener.OnClickFavorite(tempFavorite);
                    }
                }
            });
        } else if (viewHolder instanceof ProgressViewHolder) {
            if (limit == i) {
                ((ProgressViewHolder) viewHolder).progressBar.setVisibility(View.GONE);
            } else {
                ((ProgressViewHolder) viewHolder).progressBar.setIndeterminate(true);
            }
        }
    }

    @Override
    public int getItemCount() {
        if(mList.size() == 0 ) return 0;
        return mList.size() + 1;
    }

    public void setList(ArrayList<Company> list) {
        mList = list;
    }

    public void setTempFavorite(ArrayList<Company> tempFavorite) {
        this.tempFavorite = tempFavorite;
    }

    public interface OnClickFavoriteListener {

        void OnClickFavorite(ArrayList<Company> listTobeSaved);
    }
    public void setOnClickFavoriteListener(OnClickFavoriteListener onClickFavoriteListener) {
        this.onClickFavoriteListener = onClickFavoriteListener;
    }

    public void clear() {
        if(mList!=null)
            mList.clear();
    }

    public void addItems(ArrayList<Company> list) {
        mList.addAll(list);
    }
}
