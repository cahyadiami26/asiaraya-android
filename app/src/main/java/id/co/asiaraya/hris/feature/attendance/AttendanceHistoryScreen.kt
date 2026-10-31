package id.co.asiaraya.hris.feature.attendance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.co.asiaraya.hris.core.model.AttendanceRecord
import id.co.asiaraya.hris.core.model.AttendanceSummary
import id.co.asiaraya.hris.core.ui.components.*
import id.co.asiaraya.hris.core.ui.theme.*

@Composable
fun AttendanceHistoryScreen(
    summary: AttendanceSummary?,
    records: List<AttendanceRecord>,
    onBack: () -> Unit
) {
    var selectedMonth by remember { mutableStateOf("Oktober 2026") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBg)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = Ink
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Riwayat & Rekap Presensi",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Navy900
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Month Picker Bar
            item {
                CardSurface {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { /* Previous month */ }) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "Bulan lalu", tint = Ink)
                        }
                        Text(
                            text = selectedMonth,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Navy900
                        )
                        IconButton(onClick = { /* Next month */ }) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "Bulan berikutnya", tint = Ink)
                        }
                    }
                }
            }

            // Summary KPI 4-grid
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        KpiTile(
                            label = "Hadir",
                            value = "${summary?.present ?: 21} Hari",
                            icon = Icons.Default.CheckCircle,
                            modifier = Modifier.weight(1f)
                        )
                        KpiTile(
                            label = "Terlambat",
                            value = "${summary?.late ?: 1} Kali",
                            icon = Icons.Default.Schedule,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        KpiTile(
                            label = "Absen / Alpha",
                            value = "${summary?.absent ?: 0} Hari",
                            icon = Icons.Default.Cancel,
                            modifier = Modifier.weight(1f)
                        )
                        KpiTile(
                            label = "Jam Kerja",
                            value = "${summary?.workHours ?: 176.0} Jam",
                            icon = Icons.Default.Timelapse,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Daily History Header
            item {
                Text(
                    text = "Daftar Kehadiran Harian",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Navy900
                )
            }

            // Daily Items
            if (records.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Default.DateRange,
                        title = "Belum Ada Riwayat",
                        body = "Data riwayat presensi belum tersedia untuk bulan ini."
                    )
                }
            } else {
                items(records) { item ->
                    DailyAttendanceCard(item)
                }
            }
        }
    }
}

@Composable
private fun DailyAttendanceCard(record: AttendanceRecord) {
    CardSurface {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = Formatters.formatDateShort(record.date),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Ink
                )
                Text(
                    text = if (record.checkedIn) "Jam Masuk: ${Formatters.formatTime(record.checkInAt ?: "--:--")}" else "Belum check-in",
                    style = MaterialTheme.typography.bodySmall,
                    color = Muted
                )
            }
            StatusBadge(status = record.status ?: "PENDING")
        }

        if (record.checkedIn || record.checkedOut) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Keluar: ${Formatters.formatTime(record.checkOutAt ?: "--:--")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Ink2
                )
                if (record.lateMinutes > 0) {
                    Text(
                        text = "Terlambat ${record.lateMinutes} mnt",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = Amber600
                    )
                } else {
                    Text(
                        text = "Tepat Waktu",
                        style = MaterialTheme.typography.bodySmall,
                        color = Teal700
                    )
                }
            }
        }
    }
}
