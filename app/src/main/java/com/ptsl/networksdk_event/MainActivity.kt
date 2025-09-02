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

        findViewById<Button>(R.id.event_1).setOnClickListener {
//            handleEventClick("Event-1")
            val currentDate = SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()
            ).format(System.currentTimeMillis())
            networkDataUploader.startUploading(
                "PLN-101",// You can provide the PNL ID Here.
                "10.0.0",
                currentDate,
                "Event-1",
            ){ success ->
                if (success) {
                    Log.i("UploadStatus", "SDK started successfully for Event-1")
                } else {
                    Log.e("UploadStatus", "SDK failed to start for Event-1")
                }
            }

        }
    }
}


