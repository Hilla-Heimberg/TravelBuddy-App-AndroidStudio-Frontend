package com.example.myapplication

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.content.Context
import com.example.myapplication.Network.TravelSectionResponseBody
import com.example.myapplication.data.RemoteContentClient
import com.example.myapplication.data.TravelBuddyModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.junit.Before
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Test
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class TravelBuddyModelTest {
    @get:Rule
    val instantLiveData = InstantTaskExecutorRule()

    @Before
    fun before() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun after() {
        Dispatchers.resetMain()
    }

    @Test
    fun getSectionContent_withInternetConnection() = runTest {
        val model = TravelBuddyModel(FakeTravelBuddyClient())
        val sectionContent = model.getSectionContent(position)
        assertEquals(sectionContent, travelSectionResponseBody)
    }

    @Test
    fun getSectionContent_withoutInternetConnection() = runTest {
        val fakeTravelBuddyClient = FakeTravelBuddyClient()
        fakeTravelBuddyClient.isInternetConnection = false

        val model = TravelBuddyModel(fakeTravelBuddyClient)

        val sectionContent = model.getSectionContent(position)
        assertEquals(sectionContent, travelSectionNoInternetResponseBody)
    }

    @Test
    fun updateSectionContentModel_withInternetConnection() = runTest {
        val model = TravelBuddyModel(FakeTravelBuddyClient())
        val sectionContent = model.putSectionContent(defaultSection, defaultContent)
        assertEquals(sectionContent, travelSectionResponseBody)

    }

    @Test
    fun getSectionContentModel_withoutInternetConnection() = runTest {
        val fakeTravelBuddyClient = FakeTravelBuddyClient()
        fakeTravelBuddyClient.isInternetConnection = false

        val model = TravelBuddyModel(fakeTravelBuddyClient)

        val sectionContent = model.putSectionContent(defaultSection, defaultContent)
        assertEquals(sectionContent, travelSectionNoInternetResponseBody)
    }

    companion object {
        private const val defaultSection = "flights"
        private const val defaultContent = "AirFrance flight to Paris"
        private const val position = 0

        private val travelSectionResponseBody =
            TravelSectionResponseBody("flights", "AirFrance flight to Paris")
        private val travelSectionNoInternetResponseBody = TravelSectionResponseBody(errorCode = 502)

        private class FakeTravelBuddyClient(override val context: Context? = null): RemoteContentClient {
            var isInternetConnection: Boolean = true

            override suspend fun getSectionContent(
                endpoint: String,
                userId: String,
                section: String,
            ): TravelSectionResponseBody{
                return if (isInternetConnection) {
                    travelSectionResponseBody
                } else {
                    travelSectionNoInternetResponseBody
                }
            }

            override suspend fun updateSectionContent(
                endpoint: String,
                userId: String,
                section: String,
                content: String
            ) : TravelSectionResponseBody{
                return if (isInternetConnection) {
                    travelSectionResponseBody
                } else {
                    travelSectionNoInternetResponseBody
                }
            }
        }
    }
}


