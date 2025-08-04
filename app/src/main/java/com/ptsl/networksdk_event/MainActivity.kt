package com.ptsl.networksdk_event

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.ptsl.network_sdk.NetworkDataUploader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var networkDataUploader: NetworkDataUploader

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        const val PERMISSION_REQUEST_CODE = 1001
    }

    private var pendingEventName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        networkDataUploader.init(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        findViewById<Button>(R.id.event_1).setOnClickListener {
            handleEventClick("Event-1")
        }

        findViewById<Button>(R.id.event_2).setOnClickListener {
            handleEventClick("Event-2")
        }

        findViewById<Button>(R.id.event_3).setOnClickListener {
            handleEventClick("Event-3")
        }
    }

    private fun handleEventClick(eventName: String) {
        if (!hasRequiredPermissions()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE
                ),
                PERMISSION_REQUEST_CODE
            )
            pendingEventName = eventName
        } else {
            startUploading(eventName)
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.READ_PHONE_STATE
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startUploading(eventName: String) {
        if (!hasRequiredPermissions()) {
            Log.e("Permission", "Required permissions not granted")
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                val currentDate = SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()
                ).format(System.currentTimeMillis())

                networkDataUploader.startUploading(
                    "PLN-101",// You can provide the PNL ID Here.
                    "10.0.0",
                    currentDate,
                    eventName,
                ) { success ->
                    if (success) {
                        Log.i("UploadStatus", "SDK started successfully for $eventName")
                    } else {
                        Log.e("UploadStatus", "SDK failed to start for $eventName")
                    }
                }
            } else {
                Log.e("Location", "Location is null")
            }
        }.addOnFailureListener {
            Log.e("Location", "Failed to get location: ${it.message}")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                pendingEventName?.let {
                    startUploading(it)
                    pendingEventName = null
                }
            } else {
                Log.e("Permission", "Required permissions denied")
            }
        }
    }
}
