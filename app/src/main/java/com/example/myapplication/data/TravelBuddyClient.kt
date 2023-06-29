package com.example.myapplication.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.myapplication.Network.TravelBuddyApi
import com.example.myapplication.Network.TravelSectionRequestBody
import com.example.myapplication.Network.TravelSectionResponseBody
import retrofit2.Response

class TravelBuddyClient (
    override val context: Context,
    private val travelBuddyApi: TravelBuddyApi
    ) : RemoteContentClient {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    companion object {
        private val TRANSPORT = listOf<Int>(
            NetworkCapabilities.TRANSPORT_CELLULAR,
            NetworkCapabilities.TRANSPORT_WIFI,
            NetworkCapabilities.TRANSPORT_ETHERNET
        )
    }

    override suspend fun getSectionContent(
        endpoint: String,
        userId: String,
        section: String,
    ): TravelSectionResponseBody {
        return executeRequest {
            travelBuddyApi.getSectionData(endpoint, userId, section)
        }
    }

    override suspend fun updateSectionContent(
        endpoint: String,
        userId: String,
        section: String,
        content: String
    ): TravelSectionResponseBody {
        return executeRequest {
            travelBuddyApi.putSectionData(endpoint, TravelSectionRequestBody(userId, section, content))
        }
    }

    private suspend fun executeRequest(
        request: suspend () -> Response<TravelSectionResponseBody>
    ): TravelSectionResponseBody {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        // No internet connection
        if (capabilities == null || !TRANSPORT.any { capabilities.hasTransport(it) }) {
            return TravelSectionResponseBody(errorCode = 502)
        }

        val response = request() // Make Network call

        return if (response.isSuccessful) {
            response.body()!!
        } else {
            TravelSectionResponseBody(errorCode = response.code())
        }
    }
}

internal class TravelBuddyError(val statusCode: Int) : Exception("Failed with status code: ${statusCode}")
