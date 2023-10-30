package com.lusle.android.soon.model.schema

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Backdrop(
    @SerializedName("aspect_ratio")
    @Expose
    val aspectRatio: Double,

    @SerializedName("file_path")
    @Expose
    val filePath: String,

    @SerializedName("height")
    @Expose
    val height: Int,

    @SerializedName("iso_639_1")
    @Expose
    val iso6391: String?,

    @SerializedName("vote_average")
    @Expose
    val voteAverage: Int,

    @SerializedName("vote_count")
    @Expose
    val voteCount: Int,

    @SerializedName("width")
    @Expose
    val width: Int
): Serializable {
}