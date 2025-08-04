package com.ptsl.rso.network_service.data_model.logger

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
@Entity
data class EventLogModel(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @SerializedName("log_source") val logSource: String,
    @SerializedName("event_type") val eventType: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("stack_trace") val stackTrace: String,
    @SerializedName("os") val os: String,
    @SerializedName("device_model") val deviceModel: String,
) : Parcelable
