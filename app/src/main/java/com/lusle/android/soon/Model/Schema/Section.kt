package com.lusle.android.soon.Model.Schema

data class Section(
        val title:String,
        val viewType:Int
) {
    companion object {
        val VIEW_COMPANY = 0
        val VIEW_MOVIE = 1
    }
}