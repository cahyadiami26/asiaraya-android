package id.co.asiaraya.hris.feature.approval

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import id.co.asiaraya.hris.core.model.ApprovalItem
import id.co.asiaraya.hris.core.ui.components.*
import id.co.asiaraya.hris.core.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ApprovalScreen(
    approvals: List<ApprovalItem>,
    onDecide: suspend (type: String, id: Long, decision: String, note: String?) -> Result<Unit>,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedType by remember { mutableStateOf("all") } // all, leave, overtime, schedule, claim
    var selectedItemForDecision by remember { mutableStateOf<Pair<ApprovalItem, String>?>(null) } // item, decision

    val types = listOf(
        "all" to "Semua",
        "leave" to "Cuti",
        "overtime" to "Lembur",
        "schedule" to "Jadwal",
        "claim" to "Klaim"
    )

    val filtered = approvals.filter {
        selectedType == "all" || it.type.equals(selectedType, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
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
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = Ink)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Kotak Persetujuan Atasan",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Navy900
            )
        }

        // Filter Type Tabs
        ScrollableTabRow(
            selectedTabIndex = types.indexOfFirst { it.first == selectedType }.coerceAtLeast(0),
            containerColor = CardBg,
            contentColor = Teal700,
            edgePadding = 16.dp
        ) {
            types.forEach { (typeKey, typeLabel) ->
                val count = approvals.count { typeKey == "all" || it.type.equals(typeKey, ignoreCase = true) }
                Tab(
                    selected = selectedType == typeKey,
                    onClick = { selectedType = typeKey },
                    text = {
                        Text(
                            text = "$typeLabel ($count)",
                            fontWeight = if (selectedType == typeKey) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (filtered.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Default.DoneAll,
                        title = "Tidak Ada Pengajuan Menunggu",
                        body = "Semua pengajuan telah Anda setujui atau tolak. Kerja bagus!"
                    )
                }
            } else {
                items(filtered) { item ->
                    ApprovalCard(
                        item = item,
                        onApprove = { selectedItemForDecision = Pair(item, "APPROVED") },
                        onReject = { selectedItemForDecision = Pair(item, "REJECTED") }
                    )
                }
            }
        }
    }

    // Decision Note Dialog
    selectedItemForDecision?.let { (item, decision) ->
        var note by remember { mutableStateOf("") }
        val isApprove = decision == "APPROVED"

        AlertDialog(
            onDismissRequest = { selectedItemForDecision = null },
            shape = DialogShape,
            containerColor = CardBg,
            title = {
                Text(
                    text = if (isApprove) "Setujui ${item.typeLabel}?" else "Tolak ${item.typeLabel}?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isApprove) Teal700 else Rose600
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Pengajuan oleh ${item.employee?.fullName ?: "Karyawan"} (${item.employee?.position ?: ""})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Ink
                    )
                    HrisTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = "Catatan Keputusan (Opsional)",
                        placeholder = if (isApprove) "Disetujui sesuai koordinasi" else "Alasan penolakan",
                        singleLine = false,
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                if (isApprove) {
                    TealButton(
                        text = "Konfirmasi Setuju",
                        onClick = {
                            coroutineScope.launch {
                                onDecide(item.type, item.id, "APPROVED", note.trim())
                                selectedItemForDecision = null
                            }
                        }
                    )
                } else {
                    DangerButton(
                        text = "Konfirmasi Tolak",
                        onClick = {
                            coroutineScope.launch {
                                onDecide(item.type, item.id, "REJECTED", note.trim())
                                selectedItemForDecision = null
                            }
                        }
                    )
                }
            },
            dismissButton = {
                OutlineButton(
                    text = "Batal",
                    onClick = { selectedItemForDecision = null }
                )
            }
        )
    }
}

@Composable
private fun ApprovalCard(
    item: ApprovalItem,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    CardSurface {
        // Employee Info Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EmployeeAvatar(
                name = item.employee?.fullName ?: "EM",
                photoUrl = item.employee?.photoUrl,
                size = 44.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.employee?.fullName ?: "Karyawan",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Ink
                )
                Text(
                    text = "${item.employee?.position ?: "-"} · ${item.employee?.department ?: "-"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Muted
                )
            }
            StatusBadge(status = item.status, customLabel = item.statusLabel)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Request Detail Box
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Bg2)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = item.typeLabel,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Teal700
            )
            item.detail.forEach { (key, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = key, style = MaterialTheme.typography.bodySmall, color = Muted)
                    Text(text = value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = Ink2)
                }
            }
            if (!item.reason.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Alasan: ${item.reason}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Ink
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Action Buttons: Tolak & Setujui
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlineButton(
                text = "Tolak",
                onClick = onReject,
                modifier = Modifier.weight(1f)
            )
            TealButton(
                text = "Setujui",
                onClick = onApprove,
                icon = Icons.Default.Check,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
