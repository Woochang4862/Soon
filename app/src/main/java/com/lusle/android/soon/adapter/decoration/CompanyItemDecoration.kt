package com.lusle.android.soon.adapter.decoration

import android.app.Activity
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.lusle.android.soon.R

class CompanyItemDecoration @JvmOverloads constructor(
    activity: Activity,
    private val spanCount: Int = 3,
    spacing: Float = 12f, // 아이템 간의 여백 (좌우, 위아래)
    outerMargin: Float = 25f // 리스트 윗끝 아랫끝 여백
) : ItemDecoration() {
    private val spacing: Int
    private val outerMargin: Int

    init {
        if (spanCount == -1){ // horizontal flow

        }
        this.spacing = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            spacing, activity.resources.displayMetrics
        ).toInt()
        this.outerMargin = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            outerMargin, activity.resources.displayMetrics
        ).toInt()
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (spanCount == -1){
            val maxCount = parent.adapter!!.itemCount
            val position = parent.getChildAdapterPosition(view)
            val row = position / spanCount
            val firstRow = 0
            val lastRow = (maxCount - 1) / spanCount
            outRect.left = spacing / 2
            outRect.right = spacing / 2
            if (row == firstRow) {
                outRect.left = outerMargin
            }
            if (row == lastRow) {
                outRect.right = outerMargin
            }
        } else {
            val maxCount = parent.adapter!!.itemCount
            val position = parent.getChildAdapterPosition(view)
            val column = position % spanCount
            val row = position / spanCount
            val firstRow = 0
            val lastRow = (maxCount - 1) / spanCount
            outRect.left = column * spacing / spanCount
            outRect.right = spacing - (column + 1) * spacing / spanCount
            outRect.top = spacing
            if (row == firstRow) {
                outRect.top = outerMargin
            }
            if (row == lastRow) {
                outRect.bottom = outerMargin
            }
        }
    }
}
