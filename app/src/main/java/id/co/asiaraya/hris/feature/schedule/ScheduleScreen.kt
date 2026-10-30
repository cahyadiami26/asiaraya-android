package id.co.asiaraya.hris.feature.schedule

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.co.asiaraya.hris.core.model.*
import id.co.asiaraya.hris.core.ui.components.*
import id.co.asiaraya.hris.core.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ScheduleScreen(
    scheduleToday: ScheduleTodayResponse?,
    availableShifts: List<Shift>,
    requests: List<ScheduleRequest>,
    onCreateRequest: suspend (CreateScheduleRequest) -> Result<ScheduleRequest>,
    onCancelRequest: suspend (Long) -> Result<Unit>
) {
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Today Shift Hero Card
        item {
            CardSurface {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Shift Anda Hari Ini",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Navy900
                        )
                        Text(
                            text = Formatters.formatDateLong(scheduleToday?.date ?: "2026-10-30"),
                            style = MaterialTheme.typography.bodySmall,
                            color = Muted
                        )
                    }
                    val opType = scheduleToday?.shift?.operationalType ?: "NORMAL"
                    StatusBadge(status = if (opType == "NORMAL") "PRESENT" else "WFH", customLabel = opType)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Teal50)
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = Teal700,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = scheduleToday?.shift?.name ?: "Shift Pagi",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Teal700
                        )
                        Text(
                            text = "${scheduleToday?.shift?.startTime ?: "08:00"} - ${scheduleToday?.shift?.endTime ?: "17:00"} WIB · Istirahat 60 mnt",
                            style = MaterialTheme.typography.bodySmall,
                            color = Ink2
                        )
                    }
                }
            }
        }

        // 7-day schedule strip
        item {
            SectionCard(
                title = "Jadwal 7 Hari Ke Depan",
                subtitle = "Kalender rotasi shift kerja Anda"
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(scheduleToday?.nextDays ?: emptyList()) { day ->
                        ScheduleDayTile(day)
                    }
                }
            }
        }

        // Schedule Requests Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pengajuan Perubahan Jadwal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Navy900
                )
                SoftButton(
                    text = "+ Ajukan",
                    onClick = { showDialog = true }
                )
            }
        }

        if (requests.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Default.EventRepeat,
                    title = "Tidak Ada Pengajuan Terbuka",
                    body = "Anda belum mengajukan perubahan shift kerja.",
                    ctaText = "Ajukan Perubahan Jadwal",
                    onCtaClick = { showDialog = true }
                )
            }
        } else {
            items(requests) { req ->
                CardSurface {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = req.requestedShift?.name ?: "Tukar Shift",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Ink
                            )
                            Text(
                                text = "Tanggal: ${Formatters.formatDateShort(req.startDate)} - ${Formatters.formatDateShort(req.endDate)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Muted
                            )
                            if (req.reason.isNotBlank()) {
                                Text(
                                    text = "Alasan: ${req.reason}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Ink2
                                )
                            }
                        }
                        StatusBadge(status = req.status, customLabel = req.statusLabel)
                    }

                    if (req.status == "PENDING") {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            OutlineButton(
                                text = "Batalkan",
                                onClick = {
                                    coroutineScope.launch {
                                        onCancelRequest(req.id)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        ScheduleRequestDialog(
            shifts = availableShifts,
            onDismiss = { showDialog = false },
            onSubmit = { req ->
                coroutineScope.launch {
                    onCreateRequest(req)
                    showDialog = false
                }
            }
        )
    }
}

@Composable
private fun ScheduleDayTile(day: ScheduleDay) {
    val isWork = day.isWork
    val bg = if (isWork) CardBg else Slate50
    val border = if (isWork) BorderStroke(1.dp, Line) else BorderStroke(1.dp, Slate600.copy(alpha = 0.2f))

    Box(
        modifier = Modifier
            .width(100.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .border(border, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = Formatters.formatDateShort(day.date).take(6),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (isWork) Ink else Slate600
            )
            Spacer(modifier = Modifier.height(6.dp))
            if (isWork) {
                Text(
                    text = day.shift?.name?.take(10) ?: "Pagi",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                    color = Teal700
                )
                Text(
                    text = day.shift?.startTime ?: "08:00",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = Muted
                )
            } else {
                Text(
                    text = "LIBUR",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                    color = Slate600
                )
                Text(
                    text = "Off Duty",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = Muted2
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleRequestDialog(
    shifts: List<Shift>,
    onDismiss: () -> Unit,
    onSubmit: (CreateScheduleRequest) -> Unit
) {
    var selectedShiftId by remember { mutableStateOf(shifts.firstOrNull()?.id ?: 2L) }
    var startDate by remember { mutableStateOf("2026-11-05") }
    var endDate by remember { mutableStateOf("2026-11-05") }
    var reason by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = DialogShape,
        containerColor = CardBg,
        title = {
            Text(
                text = "Ajukan Perubahan Jadwal",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Navy900
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Pilih Shift Pengganti:",
                    style = MaterialTheme.typography.labelMedium,
                    color = Ink2
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    shifts.forEach { shift ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedShiftId == shift.id) Teal50 else Bg2)
                                .clickable { selectedShiftId = shift.id }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedShiftId == shift.id,
                                onClick = { selectedShiftId = shift.id }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = shift.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = Ink
                                )
                                Text(
                                    text = "${shift.startTime} - ${shift.endTime} WIB",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Muted
                                )
                            }
                        }
                    }
                }

                HrisTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = "Tanggal Mulai (YYYY-MM-DD)",
                    placeholder = "2026-11-05"
                )

                HrisTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = "Tanggal Selesai (YYYY-MM-DD)",
                    placeholder = "2026-11-05"
                )

                HrisTextField(
                    value = reason,
                    onValueChange = {
                        reason = it
                        error = null
                    },
                    label = "Alasan Pengajuan",
                    placeholder = "Jelaskan keperluan tukar shift",
                    singleLine = false,
                    maxLines = 3
                )

                if (error != null) {
                    Text(
                        text = error!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = Rose600
                    )
                }
            }
        },
        confirmButton = {
            TealButton(
                text = "Kirim Pengajuan",
                onClick = {
                    if (reason.isBlank()) {
                        error = "Alasan pengajuan wajib diisi"
                    } else {
                        onSubmit(
                            CreateScheduleRequest(
                                requestedShiftId = selectedShiftId,
                                startDate = startDate,
                                endDate = endDate,
                                reason = reason.trim()
                            )
                        )
                    }
                }
            )
        },
        dismissButton = {
            OutlineButton(
                text = "Batal",
                onClick = onDismiss
            )
        }
    )
}
