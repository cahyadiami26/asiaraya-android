package id.co.asiaraya.hris.feature.overtime

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.co.asiaraya.hris.core.model.CreateOvertimeRequest
import id.co.asiaraya.hris.core.model.Overtime
import id.co.asiaraya.hris.core.ui.components.*
import id.co.asiaraya.hris.core.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun OvertimeScreen(
    overtimes: List<Overtime>,
    onCreateOvertime: suspend (CreateOvertimeRequest) -> Result<Overtime>,
    onCancelOvertime: suspend (Long) -> Result<Unit>,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }

    val totalHours = overtimes.filter { it.status == "APPROVED" }.sumOf { it.hours }
    val totalCompensation = overtimes.filter { it.status == "APPROVED" }.sumOf { it.compensation }

    Scaffold(
        containerColor = Bg,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showDialog = true },
                containerColor = Teal600,
                contentColor = CardBg,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Ajukan Lembur", fontWeight = FontWeight.SemiBold) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardBg)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Ink)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Pengajuan Lembur",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Navy900
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Summary KPI Row
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        KpiTile(
                            label = "Lembur Disetujui",
                            value = "${totalHours.toInt()} Jam",
                            icon = Icons.Default.Timelapse,
                            modifier = Modifier.weight(1f)
                        )
                        KpiTile(
                            label = "Estimasi Uang Lembur",
                            value = Formatters.formatRupiahCompact(totalCompensation),
                            icon = Icons.Default.Payments,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Header
                item {
                    Text(
                        text = "Riwayat Pengajuan Lembur",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Navy900
                    )
                }

                if (overtimes.isEmpty()) {
                    item {
                        EmptyState(
                            icon = Icons.Default.MoreTime,
                            title = "Belum Ada Pengajuan Lembur",
                            body = "Ajukan kompensasi kerja lembur untuk shift di luar jam operasional normal.",
                            ctaText = "Ajukan Sekarang",
                            onCtaClick = { showDialog = true }
                        )
                    }
                } else {
                    items(overtimes) { item ->
                        CardSurface {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = Formatters.formatDateShort(item.workDate),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Ink
                                    )
                                    Text(
                                        text = "Rencana: ${item.plannedStart} - ${item.plannedEnd} (${item.hours} jam)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Muted
                                    )
                                }
                                StatusBadge(status = item.status, customLabel = item.statusLabel)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Alasan: ${item.reason}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Ink2
                            )

                            if (item.compensation > 0) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Kompensasi: ${Formatters.formatRupiah(item.compensation)}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = Teal700
                                )
                            }

                            if (item.status == "PENDING" || item.status == "MANAGER_APPROVED") {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                    OutlineButton(
                                        text = "Batalkan",
                                        onClick = {
                                            coroutineScope.launch {
                                                onCancelOvertime(item.id)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        OvertimeRequestDialog(
            onDismiss = { showDialog = false },
            onSubmit = { req ->
                coroutineScope.launch {
                    onCreateOvertime(req)
                    showDialog = false
                }
            }
        )
    }
}

@Composable
fun OvertimeRequestDialog(
    onDismiss: () -> Unit,
    onSubmit: (CreateOvertimeRequest) -> Unit
) {
    var workDate by remember { mutableStateOf("2026-10-31") }
    var plannedStart by remember { mutableStateOf("18:00") }
    var plannedEnd by remember { mutableStateOf("21:00") }
    var reason by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = DialogShape,
        containerColor = CardBg,
        title = {
            Text(
                text = "Form Pengajuan Lembur",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Navy900
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                HrisTextField(
                    value = workDate,
                    onValueChange = { workDate = it },
                    label = "Tanggal Kerja (YYYY-MM-DD)",
                    placeholder = "2026-10-31"
                )

                HrisTextField(
                    value = plannedStart,
                    onValueChange = { plannedStart = it },
                    label = "Jam Mulai (HH:mm)",
                    placeholder = "18:00"
                )

                HrisTextField(
                    value = plannedEnd,
                    onValueChange = { plannedEnd = it },
                    label = "Jam Selesai (HH:mm)",
                    placeholder = "21:00"
                )

                HrisTextField(
                    value = reason,
                    onValueChange = {
                        reason = it
                        error = null
                    },
                    label = "Alasan Kerja Lembur",
                    placeholder = "Penyelesaian tugas mendesak",
                    singleLine = false,
                    maxLines = 3
                )

                Text(
                    text = "Batas lembur kerja: minimal 30 menit, maksimal 4 jam per hari.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = Muted
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
                        error = "Alasan lembur wajib diisi"
                    } else if (plannedEnd <= plannedStart) {
                        error = "Jam selesai harus setelah jam mulai"
                    } else {
                        onSubmit(
                            CreateOvertimeRequest(
                                workDate = workDate,
                                plannedStart = plannedStart,
                                plannedEnd = plannedEnd,
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
