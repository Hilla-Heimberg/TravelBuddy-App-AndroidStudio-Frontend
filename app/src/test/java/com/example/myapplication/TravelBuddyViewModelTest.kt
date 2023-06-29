package com.example.myapplication

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.content.Context
import com.example.myapplication.Network.TravelSectionResponseBody
import com.example.myapplication.data.RemoteContentClient
import com.example.myapplication.ui.NetworkResult
import com.example.myapplication.ui.TravelBuddyViewModel
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

class TravelBuddyViewModelTest {
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

    // Tests for MenuFragment + TravelBuddyViewModel

    @Test
    fun getSectionContent_withInternetConnection() = runTest {
        val viewModel = TravelBuddyViewModel(FakeTravelBuddyClient())

        viewModel.sectionResponseLiveData.observeForever { networkResultEvent ->
            val response =
                networkResultEvent.getContentIfNotHandled() as NetworkResult.NavigateWithResponse
            val responseBody = response.responseBody
            assertEquals(responseBody, travelSectionResponseBody)
        }
        viewModel.viewHolderDidTap(menuPosition)
    }

    @Test
    fun getSectionContent_withoutInternetConnection() = runTest {
        val fakeTravelBuddyClient = FakeTravelBuddyClient()
        fakeTravelBuddyClient.isInternetConnection = false
        val viewModel = TravelBuddyViewModel(fakeTravelBuddyClient)

        viewModel.sectionResponseLiveData.observeForever { networkResultEvent ->
            val result = networkResultEvent.getContentIfNotHandled() as NetworkResult.DisplayMessage
            assertNotNull(result.messageDescriptionId)
        }
        viewModel.viewHolderDidTap(menuPosition)
    }

    @Test
    fun getSectionContent_updateProgressBar_withInternetConnection() = runTest {
        val viewModel = TravelBuddyViewModel(FakeTravelBuddyClient())

        viewModel.shouldShowProgressBar.observeForever { event ->
            assertEquals(event.getContentIfNotHandled(), Unit)
        }
        viewModel.viewHolderDidTap(menuPosition)
    }

    @Test
    fun getSectionContent_updateProgressBar_withoutInternetConnection() = runTest {
        val fakeTravelBuddyClient = FakeTravelBuddyClient()
        fakeTravelBuddyClient.isInternetConnection = false

        val viewModel = TravelBuddyViewModel(fakeTravelBuddyClient)

        viewModel.shouldShowProgressBar.observeForever { event ->
            assertEquals(event.getContentIfNotHandled(), Unit)
        }
        viewModel.viewHolderDidTap(menuPosition)
    }

    @Test
    fun getSectionContent_lastTappedSection_withInternetConnection() = runTest {
        val viewModel = TravelBuddyViewModel(FakeTravelBuddyClient())

        viewModel.viewHolderDidTap(menuPosition)
        viewModel.viewHolderDidTap(secondMenuPosition)

        assertEquals(viewModel.lastTappedSection, secondPositionTitle)
    }

    @Test
    fun getSectionContent_lastTappedSection_withoutInternetConnection() = runTest {
        val fakeTravelBuddyClient = FakeTravelBuddyClient()
        fakeTravelBuddyClient.isInternetConnection = false

        val viewModel = TravelBuddyViewModel(fakeTravelBuddyClient)

        viewModel.viewHolderDidTap(menuPosition)
        viewModel.viewHolderDidTap(secondMenuPosition)

        assertEquals(viewModel.lastTappedSection, secondPositionTitle)
    }

    // Tests for SectionFragment + TravelBuddyViewModel
    @Test
    fun updateSectionContent_withInternetConnection() = runTest {
        val viewModel = TravelBuddyViewModel(FakeTravelBuddyClient())

        viewModel.sectionResponseLiveData.observeForever { networkResultEvent ->
            val response =
                networkResultEvent.getContentIfNotHandled() as NetworkResult.NavigateWithResponse
            val responseBody = response.responseBody
            assertEquals(responseBody, travelSectionResponseBody)
        }
        viewModel.saveButtonDidTap(defaultSection, defaultContent)
    }

    @Test
    fun updateSectionContent_withoutInternetConnection() = runTest {
        val fakeTravelBuddyClient = FakeTravelBuddyClient()
        fakeTravelBuddyClient.isInternetConnection = false
        val viewModel = TravelBuddyViewModel(fakeTravelBuddyClient)

        viewModel.sectionResponseLiveData.observeForever { networkResultEvent ->
            val result = networkResultEvent.getContentIfNotHandled() as NetworkResult.DisplayMessage
            assertNotNull(result.messageDescriptionId)
        }
        viewModel.saveButtonDidTap(defaultSection, defaultContent)
    }

    @Test
    fun updateSectionContent_updateProgressBar_withInternetConnection() = runTest {
        val viewModel = TravelBuddyViewModel(FakeTravelBuddyClient())

        viewModel.shouldShowProgressBar.observeForever { event ->
            assertEquals(event.getContentIfNotHandled(), Unit)
        }
        viewModel.saveButtonDidTap(defaultSection, defaultContent)
    }

    @Test
    fun updateSectionContent_updateProgressBar_withoutInternetConnection() = runTest {
        val fakeTravelBuddyClient = FakeTravelBuddyClient()
        fakeTravelBuddyClient.isInternetConnection = false

        val viewModel = TravelBuddyViewModel(fakeTravelBuddyClient)

        viewModel.shouldShowProgressBar.observeForever { event ->
            assertEquals(event.getContentIfNotHandled(), Unit)
        }
        viewModel.saveButtonDidTap(defaultSection, defaultContent)
    }

    companion object {
        private const val menuPosition = 0
        private const val secondMenuPosition = 1
        private const val secondPositionTitle = "Accommodation"

        private const val defaultSection = "flights"
        private const val defaultContent = "AirFrance flight to Paris"

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
                content: String,
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
