package com.lusle.android.soon.model.schema

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Images (
    @SerializedName("backdrops")
    @Expose
    val backdrops: ArrayList<Backdrop>,

    @SerializedName("posters")
    @Expose
    val posters: ArrayList<Poster>
) : Serializable {
}