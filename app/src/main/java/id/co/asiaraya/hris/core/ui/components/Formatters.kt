package id.co.asiaraya.hris.core.ui.components

import androidx.compose.ui.graphics.Color
import id.co.asiaraya.hris.core.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

object Formatters {

    private val localeId = Locale("id", "ID")

    fun formatRupiah(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(localeId)
        return "Rp " + formatter.format(amount.toLong())
    }

    fun formatRupiahCompact(amount: Double): String {
        return when {
            amount >= 1_000_000_000 -> String.format(localeId, "Rp %.1f M", amount / 1_000_000_000)
            amount >= 1_000_000 -> String.format(localeId, "Rp %.1f jt", amount / 1_000_000)
            amount >= 1_000 -> String.format(localeId, "Rp %.0f rb", amount / 1_000)
            else -> formatRupiah(amount)
        }
    }

    fun formatDateShort(dateStr: String): String {
        if (dateStr.isBlank()) return "-"
        return try {
            val input = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = input.parse(dateStr.take(10)) ?: return dateStr
            val output = SimpleDateFormat("dd MMM yyyy", localeId)
            output.format(date)
        } catch (e: Exception) {
            dateStr
        }
    }

    fun formatDateLong(dateStr: String): String {
        if (dateStr.isBlank()) return "-"
        return try {
            val input = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = input.parse(dateStr.take(10)) ?: return dateStr
            val output = SimpleDateFormat("EEEE, d MMMM yyyy", localeId)
            output.format(date)
        } catch (e: Exception) {
            dateStr
        }
    }

    fun formatTime(timeStr: String): String {
        if (timeStr.isBlank()) return "--:--"
        return if (timeStr.length >= 5) timeStr.substring(0, 5) else timeStr
    }

    fun resolveStatusStyle(status: String?): StatusStyle {
        return when (status?.uppercase()) {
            "PENDING" -> StatusStyle(
                label = "Menunggu",
                textColor = Amber600,
                bgColor = Amber50
            )
            "MANAGER_APPROVED" -> StatusStyle(
                label = "Disetujui Atasan",
                textColor = Teal700,
                bgColor = Teal50
            )
            "APPROVED", "FINANCE_APPROVED", "PAID", "PRESENT" -> StatusStyle(
                label = when (status.uppercase()) {
                    "PAID" -> "Lunas"
                    "PRESENT" -> "Hadir"
                    "FINANCE_APPROVED" -> "Disetujui Finance"
                    else -> "Disetujui"
                },
                textColor = Teal700,
                bgColor = Teal50
            )
            "LATE" -> StatusStyle(
                label = "Terlambat",
                textColor = Amber600,
                bgColor = Amber50
            )
            "REJECTED", "ABSENT" -> StatusStyle(
                label = if (status.uppercase() == "ABSENT") "Tidak Hadir" else "Ditolak",
                textColor = Rose600,
                bgColor = Rose50
            )
            "CANCELLED", "OFF" -> StatusStyle(
                label = if (status.uppercase() == "OFF") "Libur" else "Dibatalkan",
                textColor = Slate600,
                bgColor = Slate50
            )
            "SCHEDULED", "LEAVE", "WFH" -> StatusStyle(
                label = when (status.uppercase()) {
                    "SCHEDULED" -> "Terjadwal"
                    "WFH" -> "WFH"
                    else -> "Cuti"
                },
                textColor = Indigo600,
                bgColor = Indigo50
            )
            else -> StatusStyle(
                label = status ?: "-",
                textColor = Muted,
                bgColor = Bg2
            )
        }
    }
}

data class StatusStyle(
    val label: String,
    val textColor: Color,
    val bgColor: Color
)
