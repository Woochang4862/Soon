package com.lusle.android.soon.Model.Schema

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CreditsResult(
        @SerializedName("id")
        @Expose
        val id:Int,
        @SerializedName("cast")
        @Expose
        val cast:ArrayList<Cast>,
        @SerializedName("crew")
        @Expose
        val crew:ArrayList<Crew>
) {

}
