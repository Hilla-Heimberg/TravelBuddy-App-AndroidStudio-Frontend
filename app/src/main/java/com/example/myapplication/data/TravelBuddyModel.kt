package com.example.myapplication.data

import com.example.myapplication.Network.TravelSectionResponseBody
import com.example.myapplication.R
import com.example.myapplication.ui.MainActivity

class TravelBuddyModel (private val travelBuddyClient: RemoteContentClient) {

    suspend fun getSectionContent(position: Int): TravelSectionResponseBody {
        val section = sectionStaticName[position]!!
        val userId = MainActivity.userId

        return if (sectionContentCache.contains(section)) {
            // Return cashed response.
            TravelSectionResponseBody(
                section = section,
                content = sectionContentCache[section]
            )
        } else {
            val response = travelBuddyClient.getSectionContent(getterEndpoint, userId, section)
            // Cache new successful result.
            if (response.content != null) {
                sectionContentCache[response.section!!] = response.content!!
            }
            response
        }
    }

    suspend fun putSectionContent(section: String, content: String): TravelSectionResponseBody {
        sectionContentCache.remove(section) // Remove old entry from cache.
        val userId = MainActivity.userId

        val response =
            travelBuddyClient.updateSectionContent(updateEndpoint, userId, section, content)
        // Cache successful results.
        if (response.content != null) {
            sectionContentCache[response.section!!] = response.content!!
        }

        return response
    }

    val sectionContentTitleData: Map<Int, String> =
        mapOf(
            0 to (this.travelBuddyClient.context?.getString(R.string.flights_title) ?: sectionStaticName[0]!!),
            1 to (this.travelBuddyClient.context?.getString(R.string.accommodation_title) ?: sectionStaticName[1]!!),
            2 to (this.travelBuddyClient.context?.getString(R.string.car_rental_title) ?: sectionStaticName[2]!!),
            3 to (this.travelBuddyClient.context?.getString(R.string.expenses_title) ?: sectionStaticName[3]!!),
            4 to (this.travelBuddyClient.context?.getString(R.string.food_drink_title) ?: sectionStaticName[4]!!),
            5 to (this.travelBuddyClient.context?.getString(R.string.equ_list_title) ?: sectionStaticName[5]!!),
            6 to (this.travelBuddyClient.context?.getString(R.string.travel_dest_title) ?: sectionStaticName[6]!!),
            7 to (this.travelBuddyClient.context?.getString(R.string.shopping_title) ?: sectionStaticName[7]!!),
            8 to (this.travelBuddyClient.context?.getString(R.string.transportation_title) ?: sectionStaticName[8]!!),
            9 to (this.travelBuddyClient.context?.getString(R.string.weather_title) ?: sectionStaticName[9]!!),
            10 to (this.travelBuddyClient.context?.getString(R.string.info_title) ?: sectionStaticName[10]!!),
            11 to ( this.travelBuddyClient.context?.getString(R.string.addition_title) ?: sectionStaticName[11]!!)
        )

    companion object{
        private const val FLIGHTS = "android.resource://com.example.myapplication/drawable/flights"
        private const val EXPENSES = "android.resource://com.example.myapplication/drawable/money"
        private const val CAR_RENTAL = "android.resource://com.example.myapplication/drawable/car"
        private const val ACCOMMODATION = "android.resource://com.example.myapplication/drawable/hotel"
        private const val TRAVEL_DEST = "android.resource://com.example.myapplication/drawable/destinations"
        private const val FOOD_DRINK = "android.resource://com.example.myapplication/drawable/food_drink"
        private const val INFO = "android.resource://com.example.myapplication/drawable/info"
        private const val EQU_LIST = "android.resource://com.example.myapplication/drawable/suitcase"
        private const val SHOPPING = "android.resource://com.example.myapplication/drawable/shopping"
        private const val WEATHER = "android.resource://com.example.myapplication/drawable/weather"
        private const val TRANSPORTATION = "android.resource://com.example.myapplication/drawable/transportation"
        private const val ADDITION = "android.resource://com.example.myapplication/drawable/addition"

        val sectionContentImageData: Map<Int, String> =
            mapOf(
                0 to FLIGHTS,
                1 to ACCOMMODATION,
                2 to CAR_RENTAL,
                3 to EXPENSES,
                4 to FOOD_DRINK,
                5 to EQU_LIST,
                6 to TRAVEL_DEST,
                7 to SHOPPING,
                8 to TRANSPORTATION,
                9 to WEATHER,
                10 to INFO,
                11 to ADDITION
            )

        val sectionStaticName: Map<Int, String> =
            mapOf(
                0 to "Flights",
                1 to "Accommodation",
                2 to "Car_rental",
                3 to "Expenses",
                4 to "Food_and_Drinks",
                5 to "Equipment_list",
                6 to "Travel_destinations",
                7 to "Shopping",
                8 to "Transportation",
                9 to "Weather",
                10 to "Important_information",
                11 to "Addition"
            )

        // Cache network calls results for faster responses.
        val sectionContentCache: MutableMap<String, String> = mutableMapOf<String, String>()

        private const val getterEndpoint = "travel-buddy-section-content-getter-lambda/"
        private const val updateEndpoint = "travel-buddy-section-content-uploader-lambda/"
    }
}
