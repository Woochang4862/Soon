package com.lusle.android.soon.Model.Schema

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class Videos (
    @SerializedName("results")
    @Expose
    val results: ArrayList<Video>
) : Serializable{
}