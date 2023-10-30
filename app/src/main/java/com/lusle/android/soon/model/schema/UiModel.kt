package com.lusle.android.soon.model.schema

enum class DataType{
    HEADER, ITEM, SECTION
}

sealed class UiModel(val type:DataType) {
    class MovieModel(val movie: Movie) : UiModel(DataType.ITEM)

    data class Header(val title: String) : UiModel(DataType.HEADER)

    class CompanyResult(val companyList: ArrayList<Company>) : UiModel(DataType.SECTION)
    data class CompanyHeader(val title:String, val isSubscribed:Boolean) : UiModel(DataType.HEADER)
}