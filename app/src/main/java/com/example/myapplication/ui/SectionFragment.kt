package com.example.myapplication.ui

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myapplication.Network.TravelBuddyApi
import com.example.myapplication.R
import com.example.myapplication.data.TravelBuddyClient

class SectionFragment : Fragment(R.layout.section_fragment){
    private lateinit var sectionsFragmentViewModel: TravelBuddyViewModel

    private lateinit var progressBar : ProgressBar
    private lateinit var sectionContentEditText: EditText

    private val args: SectionFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val appContext = requireContext().applicationContext

        sectionsFragmentViewModel = ViewModelProvider(
            this,
            TravelBuddyViewModel.Factory { TravelBuddyClient(appContext, TravelBuddyApi.instance) }
        ).get()

        setupSubviews(view)
        setupSectionContentLiveDataObserver()
    }

    private fun setupSubviews(view: View) {
        setupSectionTitle(view)
        setupSectionContentEditText(view)
        setupReturnButton(view)
        setupSaveButton(view)
        setupProgressBar(view)
    }

    private fun setupSectionTitle(view: View) {
        val sectionTitleTextView = view.findViewById<TextView>(R.id.sectionTitle)
        sectionTitleTextView.text = args.section.replace("_", " ")
    }

    private fun setupSectionContentEditText(view: View) {
        sectionContentEditText = view.findViewById<EditText>(R.id.inputText)
        val sectionContent = args.content
        sectionContentEditText.setText(sectionContent)
    }

    private fun setupReturnButton(view: View) {
        val returnButton = view.findViewById<Button>(R.id.returnButton)
        returnButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupSaveButton(view: View) {
        val saveButton = view.findViewById<Button>(R.id.saveButton)
        saveButton.setOnClickListener {
            val section = args.section
            val content = sectionContentEditText.text.toString()
            sectionsFragmentViewModel.saveButtonDidTap(section, content)
        }
    }

    private fun setupProgressBar(view: View) {
        progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        sectionsFragmentViewModel.shouldShowProgressBar.observe(viewLifecycleOwner) { event ->
            if (event.getContentIfNotHandled() != null){
                progressBar.isVisible = true
                progressBar.animate()
            }
        }
    }

    private fun setupSectionContentLiveDataObserver() {
        sectionsFragmentViewModel.sectionResponseLiveData
            .observe(viewLifecycleOwner) { responseEvent ->
                progressBar.isVisible = false
                val response = responseEvent.getContentIfNotHandled()
                if (response != null) {
                    val message =
                        getString((response as NetworkResult.DisplayMessage).messageDescriptionId)
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                }
            }
    }
}
