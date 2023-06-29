package com.example.myapplication.ui

import Event
import androidx.lifecycle.*
import com.example.myapplication.Network.TravelSectionResponseBody
import com.example.myapplication.R
import com.example.myapplication.data.RemoteContentClient
import com.example.myapplication.data.TravelBuddyClient
import com.example.myapplication.data.TravelBuddyModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Possible types of responses to a network call:
// ID of the string description resource of an error, or the fetched data.
sealed class NetworkResult {
    data class DisplayMessage(val messageDescriptionId: Int) : NetworkResult()
    data class NavigateWithResponse(val responseBody: TravelSectionResponseBody) : NetworkResult()
}

class TravelBuddyViewModel(private val travelBuddyClient: RemoteContentClient) : ViewModel() {
    private val travelBuddyModel = TravelBuddyModel(travelBuddyClient)
    private val _shouldShowProgressBar = MutableLiveData<Event<Unit>>()
    private val _sectionResponseLiveData = MutableLiveData<Event<NetworkResult>>()

    // Fires if fragment should show progress bar.
    val shouldShowProgressBar: LiveData<Event<Unit>> = _shouldShowProgressBar

    // Fires section response (ID of the string description of an error or the fetched data).
    val sectionResponseLiveData:
            LiveData<Event<NetworkResult>> = _sectionResponseLiveData

    // Title of the most recently tapped section.
    lateinit var lastTappedSection: String

    fun getViewHolderSectionTitle(position : Int) : String {
        return travelBuddyModel.sectionContentTitleData[position]!!
    }

    fun saveButtonDidTap(section: String, content: String){
        remoteContentRequest(
            request = { travelBuddyModel.putSectionContent(section, content) },
            onSuccess = { _sectionResponseLiveData.postValue(
                Event(NetworkResult.DisplayMessage(R.string.success_save_message))
            )}
        )
    }

    fun viewHolderDidTap(position : Int) {
        lastTappedSection = TravelBuddyModel.sectionStaticName[position]!!

        remoteContentRequest(
            request = { travelBuddyModel.getSectionContent(position) },
            onSuccess = { responseBody ->
                _sectionResponseLiveData.postValue(
                    Event(NetworkResult.NavigateWithResponse(responseBody))
                )
            }
        )
    }

    private fun remoteContentRequest(
        request: suspend () -> TravelSectionResponseBody,
        onSuccess: (TravelSectionResponseBody) -> Unit
    ) {
        _shouldShowProgressBar.postValue(Event(Unit))

        viewModelScope.launch(Dispatchers.IO) {
            val responseBody = request.invoke()

            when (responseBody.errorCode) {
                null -> onSuccess(responseBody) // Success in network call
                502 -> _sectionResponseLiveData.postValue(
                    Event(NetworkResult.DisplayMessage(R.string.no_internet_message))
                )
                else -> _sectionResponseLiveData.postValue(
                    Event(NetworkResult.DisplayMessage(R.string.fetch_section_content_error))
                )
            }
        }
    }

    class Factory(
        private val travelBuddyClientFactory: () -> TravelBuddyClient,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return TravelBuddyViewModel(
                travelBuddyClientFactory(),
            ) as T
        }
    }
}