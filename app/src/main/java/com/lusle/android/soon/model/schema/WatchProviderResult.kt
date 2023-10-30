package com.lusle.android.soon.model.schema

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class WatchProviderResult (
        @SerializedName("id")
        @Expose
        val id: Int,
        @SerializedName("buy")
        @Expose
        val buy: ArrayList<WatchProvider>?,
        @SerializedName("rent")
        @Expose
        val rent: ArrayList<WatchProvider>?,
        @SerializedName("flatrate")
        @Expose
        val flatrate: ArrayList<WatchProvider>?
) {

}
