package com.lusle.android.soon.model.schema

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

enum class Gender(val value:Int) {
    MALE(2), FEMALE(1)
}

abstract class Person(
        @SerializedName("adult")
        @Expose
        open val adult: Boolean,
        @SerializedName("gender")
        @Expose
        open val gender: Int?,
        @SerializedName("id")
        @Expose
        open val id: Int,
        @SerializedName("known_for_department")
        @Expose
        open val knownForDepartment: String,
        @SerializedName("name")
        @Expose
        open val name: String,
        @SerializedName("original_name")
        @Expose
        open val originalName: String,
        @SerializedName("popularity")
        @Expose
        open val popularity: Double,
        @SerializedName("profile_path")
        @Expose
        open val profilePath: String?
)