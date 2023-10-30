package com.lusle.android.soon.model.schema

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SpokenLanguage (
    @SerializedName("iso_639_1")
    @Expose
    val iso6391: String,

    @SerializedName("name")
    @Expose
    val name: String
) : Serializable {
    override fun toString(): String {
        return "SpokenLanguage{" +
                "iso6391='" + iso6391 + '\'' +
                ", name='" + name + '\'' +
                '}'
    }
}