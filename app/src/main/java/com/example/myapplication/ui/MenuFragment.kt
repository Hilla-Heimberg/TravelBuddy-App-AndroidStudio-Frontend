package com.example.myapplication.ui

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Network.TravelBuddyApi
import com.example.myapplication.R
import com.example.myapplication.data.TravelBuddyClient
import com.example.myapplication.data.TravelBuddyModel

class MenuFragment : Fragment(R.layout.menu_fragment){
    private lateinit var recyclerView : RecyclerView
    private lateinit var progressBar : ProgressBar

    private lateinit var sectionsMenuViewModel: TravelBuddyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appContext = requireContext().applicationContext

        sectionsMenuViewModel = ViewModelProvider(
            this,
            TravelBuddyViewModel.Factory { TravelBuddyClient(appContext, TravelBuddyApi.instance) }
        ).get()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        setupProgressBarObserver()
        setupNavigationObserver()

        setupRecyclerView(view)
    }

    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        recyclerView.layoutManager = GridLayoutManager(context, 3)
        if (recyclerView.adapter == null) {
            recyclerView.adapter = SectionsMenuAdapter(
                sectionsMenuViewModel::getViewHolderSectionImage,
                sectionsMenuViewModel::getViewHolderSectionTitle,
                sectionsMenuViewModel::viewHolderDidTap,
                sectionsMenuViewModel::getViewHoldersItemCount
            )
        }
    }

    private fun setupProgressBarObserver() {
        sectionsMenuViewModel.shouldShowProgressBar.observe(viewLifecycleOwner) { event ->
            if (event.getContentIfNotHandled() != null){
                progressBar.isVisible = true
                progressBar.animate()
            }
        }
    }

    private fun setupNavigationObserver() {
        sectionsMenuViewModel.sectionResponseLiveData.observe(viewLifecycleOwner) { responseEvent ->
            progressBar.isVisible = false
            val travelResponse = responseEvent.getContentIfNotHandled()
            when (travelResponse) {
                // Navigate to next fragment
                is NetworkResult.NavigateWithResponse ->  {
                    val body = travelResponse.responseBody
                    navigateIfNeeded(body.section!!, body.content ?: "")
                }

                // Display error message
                is NetworkResult.DisplayMessage -> {
                    val errorString = getString(travelResponse.messageDescriptionId)
                    Toast.makeText(context, errorString, Toast.LENGTH_LONG).show()
                }

                else -> {}
            }
        }
    }

    private fun navigateIfNeeded(section: String, remoteContent: String) {
        val action =
            MenuFragmentDirections.actionMenuFragmentToSectionFragment(section, remoteContent)

        // Navigate to section only if it is the most recently tapped one.
        if (section == sectionsMenuViewModel.lastTappedSection) {
            findNavController().navigate(action)
        }
    }
}

// MenuFragment + TravelBuddyViewModel extension
private fun TravelBuddyViewModel.getViewHolderSectionImage(position : Int) : Uri {
    return Uri.parse(TravelBuddyModel.sectionContentImageData[position])
}

private fun TravelBuddyViewModel.getViewHoldersItemCount() : Int {
    return TravelBuddyModel.sectionContentImageData.size
}
