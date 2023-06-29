package com.example.myapplication.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R

class LogoFragment : Fragment(R.layout.logo_fragment){
    private lateinit var startButton: Button
    private lateinit var editTextDestination: EditText
    private lateinit var editTextDate: EditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupStartButton(view)
        setupUserDestinationAndDate(view)
    }

    private fun setupStartButton(view: View){
        startButton = view.findViewById<Button>(R.id.startButton)
        startButton.setOnClickListener {
            saveUserDestinationAndDate()
            val action = LogoFragmentDirections.actionLogoFragmentToMenuFragment()
            findNavController().navigate(action)
        }
    }

    private fun saveUserDestinationAndDate(){
        val sharedPref = activity?.getSharedPreferences("com.example.myapplication", Context.MODE_PRIVATE)
        if (sharedPref != null) {
            sharedPref.edit().putString("userDestination", editTextDestination.text.toString()).apply()
            sharedPref.edit().putString("userDate", editTextDate.text.toString()).apply()
        }
    }

    private fun setupUserDestinationAndDate(view: View){
        editTextDestination = view.findViewById<EditText>(R.id.editTextDestination)
        editTextDate = view.findViewById<EditText>(R.id.editTextDate)

        val sharedPref = activity?.getSharedPreferences("com.example.myapplication", Context.MODE_PRIVATE)
        if (sharedPref != null) {
            editTextDestination.setText(sharedPref.getString("userDestination", ""))
            editTextDate.setText(sharedPref.getString("userDate", ""))
        }
    }
}