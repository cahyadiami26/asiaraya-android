package id.co.asiaraya.hris.feature.leave

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
fun LeaveScreen(
    balances: List<LeaveBalance>,
    leaves: List<LeaveItem>,
    onCreateLeave: suspend (CreateLeaveRequest) -> Result<LeaveItem>,
    onCancelLeave: suspend (Long) -> Result<Unit>
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Saldo, 1 = Riwayat
    var showApplyDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Bg,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showApplyDialog = true },
                containerColor = Teal600,
                contentColor = CardBg,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Ajukan Cuti", fontWeight = FontWeight.SemiBold) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tab Selector
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = CardBg,
                contentColor = Teal700
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "Saldo Cuti (2026)",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "Riwayat Pengajuan",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }

            if (selectedTab == 0) {
                // Tab Saldo Cuti
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(balances) { balance ->
                        LeaveBalanceCard(balance)
                    }
                }
            } else {
                // Tab Riwayat Pengajuan
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (leaves.isEmpty()) {
                        item {
                            EmptyState(
                                icon = Icons.Default.EventBusy,
                                title = "Belum Ada Pengajuan",
                                body = "Anda belum memiliki riwayat pengajuan cuti.",
                                ctaText = "Ajukan Cuti Baru",
                                onCtaClick = { showApplyDialog = true }
                            )
                        }
                    } else {
                        items(leaves) { item ->
                            LeaveHistoryCard(
                                item = item,
                                onCancel = {
                                    coroutineScope.launch {
                                        onCancelLeave(item.id)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showApplyDialog) {
        LeaveRequestDialog(
            balances = balances,
            onDismiss = { showApplyDialog = false },
            onSubmit = { req ->
                coroutineScope.launch {
                    onCreateLeave(req)
                    showApplyDialog = false
                    selectedTab = 1
                }
            }
        )
    }
}

@Composable
private fun LeaveBalanceCard(balance: LeaveBalance) {
    CardSurface {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = balance.typeName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Navy900
                )
                Text(
                    text = "Tahun ${balance.year} · Kode ${balance.typeCode}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Muted
                )
            }
            Text(
                text = "${balance.remaining.toInt()} Hari Tersisa",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Teal700
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Progress indicator
        LinearProgressIndicator(
            progress = { balance.progress.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = Teal600,
            trackColor = Bg2
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Total Kuota: ${balance.quota.toInt()} hari",
                style = MaterialTheme.typography.bodySmall,
                color = Muted
            )
            Text(
                text = "Terpakai: ${balance.used.toInt()} hari",
                style = MaterialTheme.typography.bodySmall,
                color = Ink2
            )
        }
    }
}

@Composable
private fun LeaveHistoryCard(
    item: LeaveItem,
    onCancel: () -> Unit
) {
    CardSurface {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.leaveType?.name ?: "Pengajuan Cuti",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Ink
                )
                Text(
                    text = "${Formatters.formatDateShort(item.startDate)} - ${Formatters.formatDateShort(item.endDate)} (${item.days.toInt()} hari)",
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

        if (item.status == "PENDING" || item.status == "MANAGER_APPROVED") {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlineButton(
                    text = "Batalkan Cuti",
                    onClick = onCancel
                )
            }
        }
    }
}

@Composable
fun LeaveRequestDialog(
    balances: List<LeaveBalance>,
    onDismiss: () -> Unit,
    onSubmit: (CreateLeaveRequest) -> Unit
) {
    var selectedTypeId by remember { mutableStateOf(balances.firstOrNull()?.leaveTypeId ?: 1L) }
    var startDate by remember { mutableStateOf("2026-11-10") }
    var endDate by remember { mutableStateOf("2026-11-12") }
    var reason by remember { mutableStateOf("") }
    var hasAttachment by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = DialogShape,
        containerColor = CardBg,
        title = {
            Text(
                text = "Form Pengajuan Cuti",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Navy900
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Pilih Jenis Cuti:",
                    style = MaterialTheme.typography.labelMedium,
                    color = Ink2
                )

                balances.forEach { b ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedTypeId == b.leaveTypeId) Teal50 else Bg2)
                            .clickable { selectedTypeId = b.leaveTypeId }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedTypeId == b.leaveTypeId,
                            onClick = { selectedTypeId = b.leaveTypeId }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${b.typeName} (Sisa ${b.remaining.toInt()} hari)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Ink
                        )
                    }
                }

                HrisTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = "Tanggal Mulai (YYYY-MM-DD)",
                    placeholder = "2026-11-10"
                )

                HrisTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = "Tanggal Selesai (YYYY-MM-DD)",
                    placeholder = "2026-11-12"
                )

                HrisTextField(
                    value = reason,
                    onValueChange = {
                        reason = it
                        error = null
                    },
                    label = "Alasan Cuti (Wajib)",
                    placeholder = "Keperluan mendesak keluarga",
                    singleLine = false,
                    maxLines = 3
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Bg2)
                        .clickable { hasAttachment = !hasAttachment }
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = hasAttachment,
                        onCheckedChange = { hasAttachment = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (hasAttachment) "Lampiran: Dokumen terlampir (PDF)" else "Sertakan lampiran surat dokter / berkas",
                        style = MaterialTheme.typography.bodySmall,
                        color = Ink2
                    )
                }

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
                text = "Kirim Cuti",
                onClick = {
                    if (reason.isBlank()) {
                        error = "Alasan cuti wajib diisi."
                    } else {
                        onSubmit(
                            CreateLeaveRequest(
                                leaveTypeId = selectedTypeId,
                                startDate = startDate,
                                endDate = endDate,
                                reason = reason.trim(),
                                attachment = if (hasAttachment) "data:application/pdf;base64,..." else null
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
