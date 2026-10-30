package id.co.asiaraya.hris.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiEnvelope<T>(
    val success: Boolean = true,
    val data: T? = null,
    val code: String? = null,
    val message: String? = null,
    val errors: Map<String, String>? = null,
    val meta: Meta? = null
)

@Serializable
data class Meta(
    @SerialName("request_id") val requestId: String? = null,
    @SerialName("server_time") val serverTime: String? = null,
    val version: String? = null,
    @SerialName("idempotent_replay") val idempotentReplay: Boolean? = null,
    @SerialName("unread_count") val unreadCount: Int? = null,
    @SerialName("has_open_request") val hasOpenRequest: Boolean? = null,
    val years: List<Int>? = null,
    @SerialName("total_net") val totalNet: Double? = null,
    val counts: Map<String, Int>? = null
)

@Serializable
data class SimpleOk(
    val message: String? = null
)
