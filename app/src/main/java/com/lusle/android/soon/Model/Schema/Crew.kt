package com.lusle.android.soon.Model.Schema

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Crew(
        @SerializedName("adult")
        @Expose
        val adult: Boolean,
        @SerializedName("gender")
        @Expose
        val gender: Int?,
        @SerializedName("id")
        @Expose
        val id: Int,
        @SerializedName("known_for_department")
        @Expose
        val knownForDepartment: String,
        @SerializedName("name")
        @Expose
        val name: String,
        @SerializedName("original_name")
        @Expose
        val originalName: String,
        @SerializedName("popularity")
        @Expose
        val popularity: Double,
        @SerializedName("profile_path")
        @Expose
        val profilePath: String?,
        @SerializedName("credit_id")
        @Expose
        val creditId: String,
        @SerializedName("department")
        @Expose
        val department: String,
        @SerializedName("job")
        @Expose
        val job: String
) {

}
