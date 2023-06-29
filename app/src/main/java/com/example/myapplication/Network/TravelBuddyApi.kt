package com.example.myapplication.Network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface TravelBuddyApi {

    @GET
    suspend fun getSectionData(
        @Url url: String,
        @Query("userId") userId: String,
        @Query("section") section: String
    ) : Response<TravelSectionResponseBody>

    @PUT
    suspend fun putSectionData(
        @Url url: String,
        @Body body: TravelSectionRequestBody
    ) : Response<TravelSectionResponseBody>

    companion object{
        private const val BASE_API_URL = "https://7dzu681lzl.execute-api.us-west-2.amazonaws.com/default/"

        val instance: TravelBuddyApi by lazy {
            val retrofit: Retrofit = createRetrofit()
            retrofit.create(TravelBuddyApi::class.java)
        }

        private fun createRetrofit(): Retrofit {

            // Create converter
            val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

            // Create logger
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            // Create client
            val httpClient = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            // Build Retrofit
            return Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .baseUrl(BASE_API_URL)
                .client(httpClient)
                .build()
        }
    }
}
