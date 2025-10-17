package com.example.luma

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.luma.ui.SensorConfigAdapter
import com.example.luma.ui.SensorItem
import com.google.android.material.appbar.MaterialToolbar


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivityLifecycle"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // load layout
        setContentView(R.layout.activity_sensor_config)



//RecyclerView vorbereiten
        val rv = findViewById<RecyclerView>(R.id.rv_sensor_configs)
        val adapter = SensorConfigAdapter()
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

// Beispiel-Daten:
        adapter.submitList(
            listOf(
                SensorItem("acc", "Beschleunigungssensor", enabled = true, hz = 50),
                SensorItem("gyro", "Gyroskop", enabled = false, hz = 100)
            )
        )






    }


    /*
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart aufgerufen")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume aufgerufen")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause aufgerufen")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop aufgerufen")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy aufgerufen")
    }
    */

}
