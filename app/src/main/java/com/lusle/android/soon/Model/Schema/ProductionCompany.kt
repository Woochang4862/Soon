package com.lusle.android.soon.Model.Schema

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProductionCompany (
    @SerializedName("id")
    @Expose
    val id: Int,

    @SerializedName("logo_path")
    @Expose
    val logoPath: String?,

    @SerializedName("name")
    @Expose
    val name: String,

    @SerializedName("origin_country")
    @Expose
    val originCountry: String
) : Serializable {
    override fun toString(): String {
        return "ProductionCompany{" +
                "id=" + id +
                ", logoPath='" + logoPath + '\'' +
                ", name='" + name + '\'' +
                ", originCountry='" + originCountry + '\'' +
                '}'
    }


}