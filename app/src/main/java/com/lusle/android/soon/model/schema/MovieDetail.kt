package com.lusle.android.soon.model.schema

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MovieDetail (
    @SerializedName("adult")
    @Expose
    val adult:Boolean,

    @SerializedName("backdrop_path")
    @Expose
    val backdropPath: String?,

    @SerializedName("belongs_to_collection")
    @Expose
    val belongsToCollection: BelongsToCollection?,

    @SerializedName("budget")
    @Expose
    val budget: Int,

    @SerializedName("genres")
    @Expose
    val genres: ArrayList<Genre>,

    @SerializedName("homepage")
    @Expose
    val homepage: String?,

    @SerializedName("id")
    @Expose
    val id: Int,

    @SerializedName("imdb_id")
    @Expose
    val imdbId: String?,

    @SerializedName("original_language")
    @Expose
    val originalLanguage: String,

    @SerializedName("original_title")
    @Expose
    val originalTitle: String,

    @SerializedName("overview")
    @Expose
    val overview: String?,

    @SerializedName("popularity")
    @Expose
    val popularity: Double,

    @SerializedName("poster_path")
    @Expose
    val posterPath: String?,

    @SerializedName("production_companies")
    @Expose
    val productionCompanies: ArrayList<ProductionCompany>,

    @SerializedName("production_countries")
    @Expose
    val productionCountries: ArrayList<ProductionCountry>,

    @SerializedName("release_date")
    @Expose
    val releaseDate: String,

    @SerializedName("revenue")
    @Expose
    val revenue: Int,

    @SerializedName("runtime")
    @Expose
    val runtime: Int?,

    @SerializedName("spoken_languages")
    @Expose
    val spokenLanguages: ArrayList<SpokenLanguage>,

    @SerializedName("status")
    @Expose
    val status: String,

    @SerializedName("tagline")
    @Expose
    val tagline: String?,

    @SerializedName("title")
    @Expose
    val title: String,

    @SerializedName("video")
    @Expose
    val video: Boolean,

    @SerializedName("vote_average")
    @Expose
    val voteAverage: Double,

    @SerializedName("vote_count")
    @Expose
    val voteCount: Int,

    @SerializedName("videos")
    @Expose
    val videos: Videos,

    @SerializedName("images")
    @Expose
    val images: Images
) : Serializable {

    override fun equals(other: Any?): Boolean = (other is MovieDetail) && id == other.id

    override fun hashCode(): Int {
        return id
    }

    override fun toString(): String {
        return "MovieDetail{" +
                "adult=" + adult +
                ", backdropPath='" + backdropPath + '\'' +
                ", belongsToCollection=" + belongsToCollection +
                ", budget=" + budget +
                ", genres=" + genres +
                ", homepage='" + homepage + '\'' +
                ", id=" + id +
                ", imdbId='" + imdbId + '\'' +
                ", originalLanguage='" + originalLanguage + '\'' +
                ", originalTitle='" + originalTitle + '\'' +
                ", overview='" + overview + '\'' +
                ", popularity=" + popularity +
                ", posterPath='" + posterPath + '\'' +
                ", productionCompanies=" + productionCompanies +
                ", productionCountries=" + productionCountries +
                ", releaseDate='" + releaseDate + '\'' +
                ", revenue=" + revenue +
                ", runtime=" + runtime +
                ", spokenLanguages=" + spokenLanguages +
                ", status='" + status + '\'' +
                ", tagline='" + tagline + '\'' +
                ", title='" + title + '\'' +
                ", video=" + video +
                ", voteAverage=" + voteAverage +
                ", voteCount=" + voteCount +
                '}'
    }

    fun toMovie(): Movie {
        return Movie(
                voteCount = voteCount,
                id = id,
                video = video,
                voteAverage = voteAverage,
                title = title,
                popularity = popularity,
                posterPath = posterPath,
                originalLanguage = originalLanguage,
                originalTitle = originalTitle,
                genreIds = genres.map { it.id },
                backdropPath = backdropPath,
                adult = adult,
                overview = overview,
                releaseDate = releaseDate
        )
    }
}