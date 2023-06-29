package com.example.myapplication.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.example.myapplication.R
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setNavigationGraph()
        setUserId()
    }

    private fun setNavigationGraph() {
        val navController = (
                    supportFragmentManager.findFragmentById(R.id.main_nav_host) as NavHostFragment
                ).navController
        val graph = navController.navInflater.inflate(R.navigation.navigation)
        navController.graph = graph
    }

    private fun setUserId(){
        val sharedPref = getSharedPreferences("com.example.myapplication", Context.MODE_PRIVATE)
        var curUserId = sharedPref.getString("userId", null)

        if (curUserId == null){
            curUserId = UUID.randomUUID().toString()
            sharedPref.edit().putString("userId", curUserId).apply()
        }

        userId = curUserId
    }

    companion object {
        var userId: String = "0"
    }
}
