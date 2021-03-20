package com.lusle.android.soon.Adapter.Holder;

import android.view.View;
import android.widget.ProgressBar;

import com.lusle.android.soon.R;

import androidx.recyclerview.widget.RecyclerView;

public class ProgressViewHolder extends RecyclerView.ViewHolder {
    public ProgressBar progressBar;

    public ProgressViewHolder(View v) {
        super(v);
        progressBar = v.findViewById(R.id.progressBar);
    }
}