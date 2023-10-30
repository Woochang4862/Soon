package com.lusle.android.soon.model.schema

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProductionCountry (
    @SerializedName("iso_3166_1")
    @Expose
    val iso31661: String,

    @SerializedName("name")
    @Expose
    val name: String
) : Serializable {
    override fun toString(): String {
        return "ProductionCountry{" +
                "iso31661='" + iso31661 + '\'' +
                ", name='" + name + '\'' +
                '}'
    }
}