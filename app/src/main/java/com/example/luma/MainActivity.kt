@file:Suppress("MissingPermission")

package com.example.luma

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView

@RequiresApi(Build.VERSION_CODES.M)
class MainActivity : AppCompatActivity() {


    private lateinit var toolbar: MaterialToolbar
    private lateinit var switchCollecting: MaterialSwitch
    private lateinit var btnStartStop: MaterialButton
    private lateinit var tvStatus: MaterialTextView
    private lateinit var etDefaultHz: TextInputEditText

    // Location (Fused)
    private lateinit var fusedClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var collecting = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //layout laden
        setContentView(R.layout.activity_sensor_config)

        //Views binden
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        switchCollecting = findViewById(R.id.switch_collecting)
        btnStartStop = findViewById(R.id.btn_start_stop)
        tvStatus = findViewById(R.id.tv_status)
        etDefaultHz = findViewById(R.id.et_default_hz)

        // ---- Fused Location einrichten ----
        fusedClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                for (loc in result.locations) {
                    val text = "Lat: %.5f, Lon: %.5f, Genauigkeit: %.1f m, Höhe: %.1f m"
                        .format(loc.latitude, loc.longitude, loc.accuracy, loc.altitude)
                    tvStatus.text = text
                }
            }
        }

        // ---- Switch: Start/Stop ----
        switchCollecting.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                startLocationUpdates()
            } else {
                stopLocationUpdates()
            }
            updateButtons()
        }

        // ---- Button: Start/Stop ----
        btnStartStop.setOnClickListener {
            if (collecting) stopLocationUpdates() else startLocationUpdates()
            updateButtons()
        }

        updateButtons()
    }


    private fun startLocationUpdates() {
        if (!hasLocationPermission()) {
            requestLocationPermission()
            return
        }


        val freqHz = etDefaultHz.text?.toString()?.toDoubleOrNull()?.takeIf { it > 0 } ?: 1.0
        // Intervall in ms (Deckel nach unten 500 ms)
        val intervalMs = (1000.0 / freqHz).toLong().coerceAtLeast(500L)

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalMs)
            .setMinUpdateDistanceMeters(1f)
            .build()

        fusedClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
        collecting = true
        tvStatus.text = "Positionssammlung aktiv… (Intervall ~${intervalMs} ms)"
    }


    private fun stopLocationUpdates() {
        if (!collecting) return
        fusedClient.removeLocationUpdates(locationCallback)
        collecting = false
        tvStatus.text = "Positionssammlung gestoppt."
    }


    private fun updateButtons() {
        btnStartStop.text = if (collecting) "Stoppen" else "Starten"
        switchCollecting.isChecked = collecting
        // Eingaben während Sammlung deaktivieren (optional)
        etDefaultHz.isEnabled = !collecting
    }


    private fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_CODE_LOCATION_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.isNotEmpty()) {
            val granted = grantResults.any { it == PackageManager.PERMISSION_GRANTED }
            if (granted) startLocationUpdates()
            else tvStatus.text = "Standortberechtigung verweigert."
            updateButtons()
        }
    }


    override fun onPause() {
        super.onPause()

        if (collecting) stopLocationUpdates()
    }

    companion object {
        private const val REQUEST_CODE_LOCATION_PERMISSION = 100
    }
}
