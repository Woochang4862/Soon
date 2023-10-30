package com.lusle.android.soon.model.schema

import java.io.Serializable;
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Movie(
        @SerializedName("vote_count")
        @Expose
        val voteCount: Int,

        @SerializedName("id")
        @Expose
        val id: Int,

        @SerializedName("video")
        @Expose
        val video: Boolean,

        @SerializedName("vote_average")
        @Expose
        val voteAverage: Double,

        @SerializedName("title")
        @Expose
        val title: String,

        @SerializedName("popularity")
        @Expose
        val popularity: Double,

        @SerializedName("poster_path")
        @Expose
        val posterPath: String?,

        @SerializedName("original_language")
        @Expose
        val originalLanguage: String,

        @SerializedName("original_title")
        @Expose
        val originalTitle: String,

        @SerializedName("genre_ids")
        @Expose
        val genreIds: List<Int>,

        @SerializedName("backdrop_path")
        @Expose
        val backdropPath: String?,

        @SerializedName("adult")
        @Expose
        val adult: Boolean,

        @SerializedName("overview")
        @Expose
        val overview: String?,

        @SerializedName("release_date")
        @Expose
        val releaseDate: String,

) : Serializable {
        override fun equals(other: Any?): Boolean = (other is Movie) && id == other.id

        override fun hashCode(): Int {
                return id
        }

        override fun toString(): String {
                return "Movie{" +
                        "voteCount=" + voteCount +
                        ", id=" + id +
                        ", video=" + video +
                        ", voteAverage=" + voteAverage +
                        ", title='" + title + '\'' +
                        ", popularity=" + popularity +
                        ", posterPath='" + posterPath + '\'' +
                        ", originalLanguage='" + originalLanguage + '\'' +
                        ", originalTitle='" + originalTitle + '\'' +
                        ", genreIds=" + genreIds +
                        ", backdropPath=" + backdropPath +
                        ", adult=" + adult +
                        ", overview='" + overview + '\'' +
                        ", releaseDate='" + releaseDate + '\'' +
                        '}'
        }
}