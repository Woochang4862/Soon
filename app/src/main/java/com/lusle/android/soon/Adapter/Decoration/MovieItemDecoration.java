package com.lusle.android.soon.Adapter.Decoration;

import android.app.Activity;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MovieItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;
    private int spacing;
    private int outerMargin;

    public MovieItemDecoration(Activity mActivity) {
        this(mActivity, 2);
    }

    public MovieItemDecoration(Activity mActivity, int spanCount) {
        this.spanCount = spanCount;
        spacing = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                12, mActivity.getResources().getDisplayMetrics());
        outerMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                25, mActivity.getResources().getDisplayMetrics());
    }

    @Override
    public void getItemOffsets(Rect outRect, @NonNull View view, RecyclerView parent, @NonNull RecyclerView.State state) {
        int maxCount = parent.getAdapter().getItemCount();
        int position = parent.getChildAdapterPosition(view);
        int column = position % spanCount;
        int row = position / spanCount;
        int firstRow = 0;
        int lastRow = (maxCount - 1) / spanCount;

        outRect.left = column * spacing / spanCount;
        outRect.right = spacing - (column + 1) * spacing / spanCount;
        outRect.top = spacing;

        if(row == firstRow) {
            outRect.top = 0;
        }

        if (row == lastRow) {
            outRect.bottom = outerMargin;
        }
    }
}
