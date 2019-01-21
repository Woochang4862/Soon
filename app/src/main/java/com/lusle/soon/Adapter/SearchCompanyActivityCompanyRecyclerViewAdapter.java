package com.lusle.soon.Adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.airbnb.lottie.LottieAnimationView;
import com.lusle.soon.Model.Company;
import com.lusle.soon.R;
import com.lusle.soon.SearchCompanyActivity;

import java.util.ArrayList;

public class SearchCompanyActivityCompanyRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Company> mList;
    private ArrayList<Company> result, tempBookmark;

    public SearchCompanyActivityCompanyRecyclerViewAdapter() {
        mList = new ArrayList<>();
        result = new ArrayList<>();
        tempBookmark = new ArrayList<>();
    }

    public void clear() {
        mList.clear();
        result.clear();
        tempBookmark.clear();
    }

    public class CompanyViewHolder extends RecyclerView.ViewHolder {

        public CheckBox mCheckBox;
        public LottieAnimationView mLottieAnimationView;

        public CompanyViewHolder(@NonNull View itemView) {
            super(itemView);
            mCheckBox = itemView.findViewById(R.id.company_recyclerview_checkbox);
            mLottieAnimationView = itemView.findViewById(R.id.company_recyclerview_bookmark);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.compay_recyclerview_item, viewGroup, false);
        return new CompanyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {
        ((CompanyViewHolder) viewHolder).mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                result.add(mList.get(i));
            } else {
                result.remove(mList.get(i));
            }
            SearchCompanyActivity.setAllCheckBox(isAllChecked());

            if (result.size() > 0)
                SearchCompanyActivity.setActivation();
            else
                SearchCompanyActivity.setDeactivation();
        });
        ((CompanyViewHolder) viewHolder).mCheckBox.setText(mList.get(i).getName());


        ((CompanyViewHolder) viewHolder).mLottieAnimationView.setOnClickListener(v -> {
            if (!((CompanyViewHolder) viewHolder).mLottieAnimationView.isAnimating()) {
                if (tempBookmark.contains(mList.get(i))) {
                    tempBookmark.remove(mList.get(i));
                    ((LottieAnimationView) v).setSpeed(-2);
                    ((LottieAnimationView) v).playAnimation();
                } else {
                    tempBookmark.add(mList.get(i));
                    ((LottieAnimationView) v).setSpeed(2);
                    ((LottieAnimationView) v).playAnimation();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mList == null) return 0;
        return mList.size();
    }

    private boolean isAllChecked() {
        return mList.size() == result.size();
    }

    public ArrayList<Company> getResult() {
        return result;
    }

    public void addItems(ArrayList<Company> list) {
        mList.addAll(list);
    }

    public void addItem(Company item) {
        mList.add(item);
    }
}
