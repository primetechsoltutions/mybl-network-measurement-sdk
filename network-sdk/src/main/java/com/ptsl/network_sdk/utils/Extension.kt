package com.ptsl.network_sdk.utils

import android.os.Build
import android.util.Log
import com.ptsl.network_sdk.data_model.entity.NetworkDataEntity
import com.ptsl.network_sdk.dl_ul_test.DownloadUploadHelper
import cz.mroczis.netmonster.core.Milliseconds
import cz.mroczis.netmonster.core.model.cell.CellCdma
import cz.mroczis.netmonster.core.model.cell.CellGsm
import cz.mroczis.netmonster.core.model.cell.CellLte
import cz.mroczis.netmonster.core.model.cell.CellNr
import cz.mroczis.netmonster.core.model.cell.CellTdscdma
import cz.mroczis.netmonster.core.model.cell.CellWcdma
import cz.mroczis.netmonster.core.model.cell.ICell
import okhttp3.ResponseBody
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Calendar.parseTime(time: String): Pair<Int, Int> {
    return try {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val parsedTime: Date = sdf.parse(time)
        val calendar = Calendar.getInstance()
        calendar.time = parsedTime
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        Pair(hour, minute)
    } catch (e: Exception) {
        Pair(1, 1)
    }
}

fun String.parseDBDateTime(): Long {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:00", Locale.getDefault())
        val parsedTime: Date = sdf.parse(this) ?: return 0L
        parsedTime.time
    } catch (e: Exception) {
        0L
    }
}

fun Date.timeInMinutes(otherDateTime: Long): Long {
    val diffInMillis = this.time - otherDateTime
    return diffInMillis / (60 * 1000)
}

//fun Long.isAfter15Minutes(): Boolean {
//    val currentTime = System.currentTimeMillis()
//    val targetTime = this
//    val diffInMillis = currentTime - targetTime
//    return diffInMillis >= 15 * 60 * 100
//}
fun Long.isAfter15Minutes(): Boolean {
    val currentTime = System.currentTimeMillis()
    val targetTime = this
    val diffInMillis = targetTime - currentTime
    return diffInMillis >= 15 * 60 * 100
}

fun Long.minutesDifferenceWithCurrentTime(): Long {
    val currentTime = System.currentTimeMillis()
    return (this - currentTime) / (60 * 1000)
}



fun Calendar.getWorkStartTime(startTime: String): Calendar {
    val calendar = Calendar.getInstance()
    val workStartTime = calendar.parseTime(startTime)
    return calendar.apply {
        set(Calendar.HOUR_OF_DAY, workStartTime.first)
        set(Calendar.MINUTE, workStartTime.second)
    }
}

fun Calendar.getWorkEndTime(endTime: String): Calendar {
    val calendar = Calendar.getInstance()
    val workEndTime = calendar.parseTime(endTime)
    return calendar.apply {
        set(Calendar.HOUR_OF_DAY, workEndTime.first)
        set(Calendar.MINUTE, workEndTime.second)
    }
}

fun Calendar.getNextDaySameTime(time: String): Calendar {
    val calendar = Calendar.getInstance()
    val HhMM = calendar.parseTime(time)
    return calendar.apply {
        add(Calendar.DATE, 1)
        set(Calendar.HOUR_OF_DAY, HhMM.first)
        set(Calendar.MINUTE, HhMM.second)
    }
}

suspend fun ICell.prepareDate(
    locationPair: Pair<Double, Double>,
    downloader: DownloadUploadHelper,
    hasMobileInternet: Boolean=false,
    activeNetworkMnc : String = "-1"
): NetworkDataEntity {
    val mcc = this.network?.mcc
    val mnc = this.network?.mnc
    Log.e("MNC", "MNC : ${mnc}")
    return when (this) {
        is CellCdma -> {
            val speedPair = downloader.getBandWidthSpeed(networkType = "2G",hasMobileInternet= hasMobileInternet,activeNetworkMnc = activeNetworkMnc, currentMnc = mnc)
            return NetworkDataEntity().also {
                it.time = getCurrentTime(this.timestamp)
                it.date = getCurrentDate(this.timestamp)
                it.mcc = "${removeNullFromString("${mcc}")}"
                it.mnc = "${removeNullFromString("${mnc}")}"
                it.type = "CDMA"
                it.band = "${removeNullFromString(this.band?.name ?: "")}"
                it.snr = removeNullFromString("${this.signal.evdoSnr}")
                it.rssi = removeNullFromString("${this.signal.cdmaRssi}")
                it.lattitude = locationPair.first
                it.longitude = locationPair.second
                it.dlspeed = speedPair.first
                it.ulspeed = speedPair.second
                it.deviceModel = "${Build.MODEL}"
                it.data = if (hasMobileInternet) "Mobile" else "Wifi"
                it.isDataCaptureOffline = false
                it.isUserDeviceOnCall = false
                it.deviceManufacture ="${Build.MANUFACTURER}"
                it.deviceOsVersion ="${Build.VERSION.SDK_INT}"
                it.usedSimSlot= 1
                it.rtt = 0.0
                it.latency =0.0
            }
        }

        is CellGsm -> {
            val speedPair = downloader.getBandWidthSpeed(networkType = "2G",hasMobileInternet= hasMobileInternet,activeNetworkMnc = activeNetworkMnc, currentMnc = mnc)
            return NetworkDataEntity().also {
                it.time = getCurrentTime(this.timestamp)
                it.date = getCurrentDate(this.timestamp)
                it.mcc = "${removeNullFromString("${mcc}")}"
                it.mnc = "${removeNullFromString("${mnc}")}"
                it.lac = removeNullFromString("${this.lac}")
                it.type = "2G"
                it.cid = removeNullFromString("${this.cid}")
                it.arfcn = removeNullFromString("${this.band?.arfcn}")
                it.ta = removeNullFromString("${this.signal.timingAdvance}")
                it.band = "${removeNullFromString(this.band?.name ?: "")}"
                it.rxlev = removeNullFromString("${this.signal.rssi}")
                it.rxQual = removeNullFromString("${this.signal.bitErrorRate?.let {rxQual -> this.calculateRXQUAL(rxQual)}}")
                it.bitRateError = removeNullFromString("${this.signal.bitErrorRate}")
                it.rssi = removeNullFromString("${this.signal.rssi}")
                it.lattitude = locationPair.first
                it.longitude = locationPair.second
                it.dlspeed = speedPair.first
                it.ulspeed = speedPair.second
                it.deviceModel = "${Build.MODEL}"
                it.data = if (hasMobileInternet) "Mobile" else "Wifi"
                it.isDataCaptureOffline = false
                it.isUserDeviceOnCall = false
                it.deviceManufacture ="${Build.MANUFACTURER}"
                it.deviceOsVersion ="${Build.VERSION.SDK_INT}"
                it.usedSimSlot= 1
                it.rtt = 0.0
                it.latency =0.0
            }
        }

        is CellWcdma -> {
            val speedPair = downloader.getBandWidthSpeed(networkType = "3G",hasMobileInternet= hasMobileInternet,activeNetworkMnc = activeNetworkMnc, currentMnc = mnc)
            return NetworkDataEntity().also {
                it.time = getCurrentTime(this.timestamp)
                it.date = getCurrentDate(this.timestamp)
                it.mcc = "${removeNullFromString("${mcc}")}"
                it.mnc = "${removeNullFromString("${mnc}")}"
                it.lac = removeNullFromString("${this.lac}")
                it.type = "3G"
                it.cid = removeNullFromString("${this.cid}")
                it.psc = removeNullFromString("${this.psc}")
                it.arfcn = removeNullFromString("${this.band?.downlinkUarfcn ?: ""}")
                it.band = "${removeNullFromString(this.band?.name ?: "")}"
                it.rscp = removeNullFromString("${this.signal.rscp}")
                it.ecNo = removeNullFromString("${this.signal.ecno}")
                it.rssi = removeNullFromString("${this.signal.rssi}")
                it.lattitude = locationPair.first
                it.longitude = locationPair.second
                it.dlspeed = speedPair.first
                it.ulspeed = speedPair.second
                it.deviceModel = "${Build.MODEL}"
                it.data = if (hasMobileInternet)"Mobile" else "Wifi"
                it.isDataCaptureOffline = false
                it.isUserDeviceOnCall = false
                it.deviceManufacture ="${Build.MANUFACTURER}"
                it.deviceOsVersion ="${Build.VERSION.SDK_INT}"
                it.usedSimSlot= 1
                it.rtt = 0.0
                it.latency =0.0
            }
        }

        is CellLte -> {
            val speedPair = downloader.getBandWidthSpeed(networkType = "4G",hasMobileInternet= hasMobileInternet,activeNetworkMnc = activeNetworkMnc, currentMnc = mnc)
            return NetworkDataEntity().also {
                it.time = getCurrentTime(this.timestamp)
                it.date = getCurrentDate(this.timestamp)
                it.mcc = "${removeNullFromString("${mcc}")}"
                it.mnc = "${removeNullFromString("${mnc}")}"
                it.tac = removeNullFromString("${this.tac}")
                it.type = "4G"
                it.cid = removeNullFromString("${this.cid}")
                it.enb = removeNullFromString("${this.enb}")
                it.pci = removeNullFromString("${this.pci}")
                it.ta = removeNullFromString("${this.signal.timingAdvance}")
                it.bw = removeNullFromString("${this.bandwidth}")
                it.arfcn = removeNullFromString("${this.band?.downlinkEarfcn}")
                it.band = "${removeNullFromString(this.band?.name ?: "")}"
                it.rsrp = removeNullFromString("${this.signal.rsrp}")
                it.rsrq = removeNullFromString("${this.signal.rsrq}")
                it.snr = removeNullFromString("${this.signal.snr}")
                it.cqi = removeNullFromString("${this.signal.cqi}")
                it.rssi = removeNullFromString("${this.signal.rssi}")
                it.lattitude = locationPair.first
                it.longitude = locationPair.second
                it.dlspeed = speedPair.first
                it.ulspeed = speedPair.second
                it.deviceModel = "${Build.MODEL}"
                it.data = if (hasMobileInternet)"Mobile" else "Wifi"
                it.isDataCaptureOffline = false
                it.isUserDeviceOnCall = false
                it.deviceManufacture ="${Build.MANUFACTURER}"
                it.deviceOsVersion ="${Build.VERSION.SDK_INT}"
                it.usedSimSlot= 1
                it.rtt = 0.0
                it.latency =0.0
            }
        }

        is CellNr -> {
            val speedPair = downloader.getBandWidthSpeed(networkType = "4G",hasMobileInternet= hasMobileInternet,activeNetworkMnc = activeNetworkMnc, currentMnc = mnc)
            return NetworkDataEntity().also {
                it.time = getCurrentTime(this.timestamp)
                it.date = getCurrentDate(this.timestamp)
                it.mcc = "${removeNullFromString("${mcc}")}"
                it.mnc = "${removeNullFromString("${mnc}")}"
                it.tac = removeNullFromString("${this.tac}")
                it.type = "5G"
                it.pci = removeNullFromString("${this.pci}")
                it.band = "${removeNullFromString(this.band?.name ?: "")}"
                it.rsrp = removeNullFromString("${this.signal.ssRsrp}")
                it.rsrq = removeNullFromString("${this.signal.ssRsrq}")
                it.snr = removeNullFromString("${this.signal.ssSinr}")
                it.lattitude = locationPair.first
                it.longitude = locationPair.second
                it.dlspeed = speedPair.first
                it.ulspeed = speedPair.second
                it.deviceModel = "${Build.MODEL}"
                it.data = if (hasMobileInternet)"Mobile" else "Wifi"
                it.isDataCaptureOffline = false
                it.isUserDeviceOnCall = false
                it.deviceManufacture ="${Build.MANUFACTURER}"
                it.deviceOsVersion ="${Build.VERSION.SDK_INT}"
                it.usedSimSlot= 1
                it.rtt = 0.0
                it.latency =0.0
            }
        }

        is CellTdscdma -> {
            val speedPair = downloader.getBandWidthSpeed(networkType = "3G",hasMobileInternet= hasMobileInternet,activeNetworkMnc = activeNetworkMnc, currentMnc = mnc)
            return NetworkDataEntity().also {
                it.time = getCurrentTime(this.timestamp)
                it.date = getCurrentDate(this.timestamp)
                it.mcc = "${removeNullFromString("${mcc}")}"
                it.mnc = "${removeNullFromString("${mnc}")}"
                it.lac = removeNullFromString("${this.cid}")
                it.type = "3G"
                it.cid = removeNullFromString("${this.cid}")
                it.band = "${removeNullFromString(this.band?.name ?: "")}"
                it.rssi = removeNullFromString("${this.signal.rssi}")
                it.lattitude = locationPair.first
                it.longitude = locationPair.second
                it.dlspeed = speedPair.first
                it.ulspeed = speedPair.second
                it.deviceModel = "${Build.MODEL}"
                it.data = if (hasMobileInternet)"Mobile" else "Wifi"
                it.isDataCaptureOffline = false
                it.isUserDeviceOnCall = false
                it.deviceManufacture ="${Build.MANUFACTURER}"
                it.deviceOsVersion ="${Build.VERSION.SDK_INT}"
                it.usedSimSlot= 1
                it.rtt = 0.0
                it.latency =0.0
            }
        }

        else -> NetworkDataEntity()
    }
}

fun ResponseBody?.getTotalBytes(): Int {
    var size = 0
    val inputStream: InputStream? = this?.byteStream()
    val byteArrayOutputStream = ByteArrayOutputStream()
    val buffer = ByteArray(1024)
    var length: Int
    var totalBytesRead = 0
    try {
        while (inputStream?.read(buffer).also { length = it ?: -1 } != -1) {
            byteArrayOutputStream.write(buffer, 0, length)
            totalBytesRead += length
        }
        val byteArray = byteArrayOutputStream.toByteArray()
        size = byteArray.size
    } catch (_: Exception) {
    } finally {
        inputStream?.close()
        byteArrayOutputStream.close()
    }
    return size
}

private fun getCurrentTime(timestamp: Milliseconds?): String {
    return "${SimpleDateFormat("yyyy-MM-dd'T'HH:mm:00").format(System.currentTimeMillis())}"
}

private fun getCurrentDate(timestamp: Milliseconds?): String {
    return "${SimpleDateFormat("yyyy-MM-dd'T'HH:mm:00").format(System.currentTimeMillis())}"
}

private fun removeNullFromString(str: String): Int {
    return try {
        str.replace("null", "").toDouble().toInt()
    } catch (e: Exception) {
        0
    }
}

private fun CellGsm.calculateRXQUAL(ber: Int): Int {
    return when {
        ber >= 0 && ber < 0.2 -> 0
        ber >= 0.2 && ber < 0.4 -> 1
        ber >= 0.4 && ber < 0.8 -> 2
        ber >= 0.8 && ber < 1.6 -> 3
        ber >= 1.6 && ber < 3.2 -> 4
        ber >= 3.2 && ber < 6.4 -> 5
        ber >= 6.4 && ber < 12.8 -> 6
        ber >= 12.8 && ber <= 100 -> 7
        else -> 0
    }
}


