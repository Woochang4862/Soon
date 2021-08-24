package com.lusle.android.soon.Adapter.Listener

import com.lusle.android.soon.Model.Schema.Company

interface OnCompanyBookMarkButtonClickListener {
    interface Adapter{
        fun onCompanyBookMarkButtonClicked(view:android.view.View, company:Company)
    }
    interface View{
        fun onCompanyBookMarkButtonClicked(favoriteCompanyList:ArrayList<Company?>)
    }
}
