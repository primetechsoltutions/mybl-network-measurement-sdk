package com.ptsl.network_sdk

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.telephony.SubscriptionManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.ptsl.network_sdk.api.ApiService
import com.ptsl.network_sdk.data_model.NetworkDataRequest
import com.ptsl.network_sdk.data_model.entity.AuthEntity
import com.ptsl.network_sdk.data_model.entity.NetworkDataEntity
import com.ptsl.network_sdk.db.NetworkDao
import com.ptsl.network_sdk.dl_ul_test.DownloadUploadHelper
import com.ptsl.network_sdk.utils.getWorkEndTime
import com.ptsl.network_sdk.utils.getWorkStartTime
import com.ptsl.network_sdk.utils.prepareDate
import com.ptsl.rso.network_service.data_model.logger.EventLogModel
import com.ptsl.rso.network_service.data_model.logger.LogDataWrapper
import cz.mroczis.netmonster.core.BuildConfig
import cz.mroczis.netmonster.core.factory.NetMonsterFactory
import cz.mroczis.netmonster.core.model.connection.PrimaryConnection
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltWorker
class NetworkDataWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val apiService: ApiService,
    private val downloader: DownloadUploadHelper,
    private val databaseDao: NetworkDao,
) : CoroutineWorker(appContext, workerParams) {

    private val locationClient = LocationServices.getFusedLocationProviderClient(appContext)

    override suspend fun doWork(): Result {
        Log.d("worker", "-------> \n Started \n <-------")
        var statusCode: Int=0
        var errorMessage: String=""
        return try {
            val locationPair = getCurrentLocation()
            val auth = getAuth()
            val dataList = getReqData(locationPair)
            if(dataList.isEmpty()){
                errorMessage="Net Monster Initialization Failed"
            }
            val response = apiService.postNetworkData(NetworkDataRequest(auth, dataList))
            Log.d("response", "✅ API success: $response")
            clearNetworkDataCache()
            if(databaseDao.getNetworkDataLogEventCount()>0){
                apiService.postRetailerNetworkDataLogs(LogDataWrapper(auth,databaseDao.getNetworkDataLogEvent()))
                clearNetworkDataCacheLog()
            }
            Result.success()
        } catch (e: Exception)
        {

            when (e) {
                is HttpException -> {
                    // Handle HTTP errors (4xx, 5xx)
                    statusCode = e.code()
                    errorMessage = "HTTP error: ${e.message}"
                }
                is IOException -> {
                    // Handle network errors
                    errorMessage = "Network error: ${e.message}"
                }
                else -> {
                    // Handle other exceptions
                    errorMessage = "Unexpected error: ${e.message}"
                }
            }

            Log.e("worker", "❌ Error: ${e.localizedMessage}", e)
            insertNetworkDataInDb()
            val auth = getAuth()
            val eventLogModel=EventLogModel(
                logSource = "mobile_app",
                eventType = "error",
                title = "Network Request Failed",
                description = "Failed to post network data",
                statusCode = statusCode,
                status = if (statusCode in 400..599) "HTTP Error" else "System Error",
                message = errorMessage,
                stackTrace = e.stackTraceToString(),
                os = Build.VERSION.SDK_INT.toString(),
                deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}"
            )
            preparedLogEventData(auth, eventLogModel)
            Result.failure()
        }
        finally {
            if(errorMessage.isNotEmpty()){
                val auth = getAuth()
                val eventLogModel=EventLogModel(
                    logSource = "mobile_app",
                    eventType = "error",
                    title = "Network Request Failed",
                    description = "Failed to post network data",
                    statusCode = statusCode,
                    status = if (statusCode in 400..599) "HTTP Error" else "System Error",
                    message = errorMessage,
                    stackTrace = "",
                    os = Build.VERSION.SDK_INT.toString(),
                    deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}"
                )
                preparedLogEventData(auth, eventLogModel)
            }

        }
    }


    private suspend fun getCurrentLocation(): Pair<Double, Double> =
        suspendCancellableCoroutine { cont ->
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                cont.resume(Pair(0.0, 0.0)) {}
                return@suspendCancellableCoroutine
            }

            locationClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                CancellationTokenSource().token
            ).addOnSuccessListener { location ->
                if (location != null) {
                    cont.resume(Pair(location.latitude, location.longitude)) {}
                } else {
                    cont.resume(Pair(0.0, 0.0)) {}
                }
            }.addOnFailureListener {
                cont.resume(Pair(0.0, 0.0)) {}
            }
        }

    private suspend fun getAuth(): AuthEntity =
        databaseDao.getPersistentAuth() ?: AuthEntity()

    private suspend fun getReqData(locationPair: Pair<Double, Double>): ArrayList<NetworkDataEntity> {
        val isMobileNetworkConnected = isMobileNetworkConnected(applicationContext)
        val activeNetworkMnc = if (isMobileNetworkConnected) getActiveNetworkMNC() else "-1"
        val dataList = arrayListOf<NetworkDataEntity>()

        NetMonsterFactory.get(applicationContext).apply {
            val cells = try {
                getCells()
            } catch (e: SecurityException) {
                Log.w("NetworkDataWorker", "Permission denied for getCells", e)
                return dataList
            }

            cells.find {
                it.network?.mcc == "470" && (it.network?.mnc == "03" || it.network?.mnc == "3")
            }?.let {
                cells.forEach {
                    if (it.connectionStatus is PrimaryConnection) {
                        val data = it.prepareDate(
                            locationPair,
                            downloader,
                            isMobileNetworkConnected,
                            activeNetworkMnc
                        )
                        dataList.add(data)
                    }
                }
            }
        }

        return dataList
    }

    private fun isMobileNetworkConnected(context: Context): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = cm.activeNetwork ?: return false
            val caps = cm.getNetworkCapabilities(network) ?: return false
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        } catch (e: Exception) {
            false
        }
    }

    private fun getActiveNetworkMNC(): String {
        var id = "-1"
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val subscriptionManager = SubscriptionManager.from(applicationContext)
                val nDataSubscriptionId = getDefaultDataSubscriptionId(subscriptionManager)

                if (nDataSubscriptionId != SubscriptionManager.INVALID_SUBSCRIPTION_ID) {
                    val si = subscriptionManager.getActiveSubscriptionInfo(nDataSubscriptionId)
                    id = si?.mnc?.toString() ?: "-1"
                }
            }

            Log.e("getActiveNetworkMNC", "Active MNC : $id")


        } catch (_: Exception) {
        }

        return "0${id}"
    }

    private fun getDefaultDataSubscriptionId(subscriptionManager: SubscriptionManager): Int {
        if (Build.VERSION.SDK_INT >= 24) {
            val nDataSubscriptionId = SubscriptionManager.getDefaultDataSubscriptionId()
            if (nDataSubscriptionId != SubscriptionManager.INVALID_SUBSCRIPTION_ID) {
                return nDataSubscriptionId
            }
        }

        try {
            val subscriptionClass = Class.forName(subscriptionManager.javaClass.name)
            try {
                val getDefaultDataSubscriptionId =
                    subscriptionClass.getMethod("getDefaultDataSubscriptionId")
                try {
                    return getDefaultDataSubscriptionId.invoke(subscriptionManager) as Int
                } catch (e1: IllegalAccessException) {
                    e1.printStackTrace()
                }
            } catch (e1: NoSuchMethodException) {
                e1.printStackTrace()
            }
        } catch (e1: ClassNotFoundException) {
            e1.printStackTrace()
        }

        return SubscriptionManager.INVALID_SUBSCRIPTION_ID
    }


    private suspend fun preparedLogEventData(auth: AuthEntity, eventLogModel:EventLogModel){
         try {
             apiService.postRetailerNetworkDataLogs(LogDataWrapper(auth, arrayListOf(eventLogModel)))

        } catch (e: Exception) {
            insertNetworkDataLogInDb(eventLogModel)
        }
    }
    private suspend fun insertNetworkDataInDb() {
        try {
            val reqData = getReqData(getCurrentLocation())
            reqData.forEach { it.isDataCaptureOffline = true }
            databaseDao.insertNetworkData(reqData)
        } catch (_: Exception) {
        }
    }

    private suspend fun clearNetworkDataCache() {
        databaseDao.deleteNetworkData()
    }
    private suspend fun clearNetworkDataCacheLog() {
        databaseDao.deleteNetworkDataLogEvent()
    }


    private suspend fun insertNetworkDataLogInDb(eventLogModel: EventLogModel) {
        try {
            databaseDao.insertNetworkDataLogIntoDB(eventLogModel)
        } catch (_: Exception) {
        }
    }

}