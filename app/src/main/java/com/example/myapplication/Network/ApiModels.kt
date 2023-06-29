package com.example.myapplication.Network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TravelSectionRequestBody(
    @Json(name = "userId")
    val userId: String,

    @Json(name = "section")
    val section: String,

    @Json(name = "content")
    val content: String? = null
)

@JsonClass(generateAdapter = true)
data class TravelSectionResponseBody(
    @Json(name = "section")
    val section: String? = null,

    @Json(name = "content")
    val content: String? = null,

    val errorCode: Int? = null
)
