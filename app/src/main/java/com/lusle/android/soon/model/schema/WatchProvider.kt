package com.lusle.android.soon.model.schema

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class WatchProvider(
        @SerializedName("display_priority")
        @Expose
        val displayPriority: Int,
        @SerializedName("logo_path")
        @Expose
        val logoPath: String,
        @SerializedName("provider_id")
        @Expose
        val providerId: Int,
        @SerializedName("provider_name")
        @Expose
        val providerName: String,
        @SerializedName("link")
        @Expose
        val link:String,
        @SerializedName("price")
        @Expose
        val price:String?,
        @SerializedName("presentation_type")
        @Expose
        val presentationType: String?
) {

}
