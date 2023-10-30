package com.lusle.android.soon.adapter.holder

import android.view.View
import com.airbnb.lottie.LottieAnimationView
import com.lusle.android.soon.R
import com.lusle.android.soon.adapter.listener.OnCompanyBookMarkButtonClickListener

class CompanyHeaderViewHolder(itemView: View, private val onCompanyBookMarkButtonClickListener: OnCompanyBookMarkButtonClickListener?) : HeaderViewHolder(itemView) {

    private val bookMarkButton: LottieAnimationView = itemView.findViewById(R.id.company_bookmark)

    fun bind(text:String, isSubscribed:Boolean){
        super.bind(text)

        bookMarkButton.visibility = View.VISIBLE
        if (isSubscribed) {
            bookMarkButton.progress = 1.0f
        } else {
            bookMarkButton.progress = 0.0f
        }
        bookMarkButton.setOnClickListener {
            if (!bookMarkButton.isAnimating) {
                if (bookMarkButton.progress != 0.0f) { // == 1.0f 를 하지 않은 이유는 부동소수점 오류때문
                    bookMarkButton.speed = -1.0f
                    bookMarkButton.playAnimation()
                    onCompanyBookMarkButtonClickListener?.onCompanyBookMarkButtonClicked(
                        bookMarkButton,
                        false
                    )
                } else if (bookMarkButton.progress == 0.0f) {
                    bookMarkButton.speed = 1.0f
                    bookMarkButton.playAnimation()
                    onCompanyBookMarkButtonClickListener?.onCompanyBookMarkButtonClicked(
                        bookMarkButton,
                        true
                    )
                }
            }
        }
    }

    companion object {
        val TAG: String = CompanyHeaderViewHolder::class.java.simpleName
    }

}
