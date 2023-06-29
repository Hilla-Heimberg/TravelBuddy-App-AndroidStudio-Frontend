package com.example.myapplication.data

import android.content.Context
import com.example.myapplication.Network.TravelSectionResponseBody

interface RemoteContentClient {
    val context : Context?

    suspend fun getSectionContent(
        endpoint: String,
        userId: String,
        section: String
    ): TravelSectionResponseBody

    suspend fun updateSectionContent(
        endpoint: String,
        userId: String,
        section: String,
        content: String
    ) : TravelSectionResponseBody
}