package id.co.asiaraya.hris.feature.claim

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.co.asiaraya.hris.core.model.Claim
import id.co.asiaraya.hris.core.model.ClaimCategory
import id.co.asiaraya.hris.core.model.CreateClaimRequest
import id.co.asiaraya.hris.core.ui.components.*
import id.co.asiaraya.hris.core.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ClaimScreen(
    claims: List<Claim>,
    categories: List<ClaimCategory>,
    onCreateClaim: suspend (CreateClaimRequest) -> Result<Claim>,
    onCancelClaim: suspend (Long) -> Result<Unit>,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }

    val totalPending = claims.filter { it.status == "PENDING" }.sumOf { it.amount }
    val totalApproved = claims.filter { it.status == "APPROVED" || it.status == "PAID" }.sumOf { it.amount }

    Scaffold(
        containerColor = Bg,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showDialog = true },
                containerColor = Teal600,
                contentColor = CardBg,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Ajukan Klaim", fontWeight = FontWeight.SemiBold) }
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
                    text = "Klaim & Reimbursement",
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
                            label = "Menunggu Persetujuan",
                            value = Formatters.formatRupiahCompact(totalPending),
                            icon = Icons.Default.HourglassTop,
                            modifier = Modifier.weight(1f)
                        )
                        KpiTile(
                            label = "Disetujui / Dibayarkan",
                            value = Formatters.formatRupiahCompact(totalApproved),
                            icon = Icons.Default.CheckCircle,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Text(
                        text = "Daftar Riwayat Klaim Biaya",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Navy900
                    )
                }

                if (claims.isEmpty()) {
                    item {
                        EmptyState(
                            icon = Icons.Default.ReceiptLong,
                            title = "Belum Ada Pengajuan Klaim",
                            body = "Klaim biaya dinas, medis, dan transportasi dapat diajukan di sini.",
                            ctaText = "Buat Pengajuan Baru",
                            onCtaClick = { showDialog = true }
                        )
                    }
                } else {
                    items(claims) { item ->
                        CardSurface {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.claimNumber,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = Muted
                                    )
                                    Text(
                                        text = item.category?.name ?: "Reimbursement",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Ink
                                    )
                                    Text(
                                        text = "Tanggal Transaksi: ${Formatters.formatDateShort(item.claimDate)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Muted
                                    )
                                }
                                StatusBadge(status = item.status, customLabel = item.statusLabel)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Ink2,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = Formatters.formatRupiah(item.amount),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Teal700
                                )
                            }

                            if (item.status == "PENDING") {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                    OutlineButton(
                                        text = "Batalkan Klaim",
                                        onClick = {
                                            coroutineScope.launch {
                                                onCancelClaim(item.id)
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
        ClaimRequestDialog(
            categories = categories,
            onDismiss = { showDialog = false },
            onSubmit = { req ->
                coroutineScope.launch {
                    onCreateClaim(req)
                    showDialog = false
                }
            }
        )
    }
}

@Composable
fun ClaimRequestDialog(
    categories: List<ClaimCategory>,
    onDismiss: () -> Unit,
    onSubmit: (CreateClaimRequest) -> Unit
) {
    var selectedCatId by remember { mutableStateOf(categories.firstOrNull()?.id ?: 1L) }
    var claimDate by remember { mutableStateOf("2026-10-30") }
    var amountText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var hasReceipt by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = DialogShape,
        containerColor = CardBg,
        title = {
            Text(
                text = "Form Pengajuan Klaim Biaya",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Navy900
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Kategori Reimbursement:",
                    style = MaterialTheme.typography.labelMedium,
                    color = Ink2
                )
                categories.forEach { cat ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedCatId == cat.id) Teal50 else Bg2)
                            .clickable { selectedCatId = cat.id }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedCatId == cat.id,
                            onClick = { selectedCatId = cat.id }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = cat.name,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = Ink
                            )
                            if (cat.maxAmount != null) {
                                Text(
                                    text = "Plafon maks: ${Formatters.formatRupiah(cat.maxAmount)}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = Muted
                                )
                            }
                        }
                    }
                }

                HrisTextField(
                    value = claimDate,
                    onValueChange = { claimDate = it },
                    label = "Tanggal Transaksi (YYYY-MM-DD)",
                    placeholder = "2026-10-30"
                )

                HrisTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it.filter { char -> char.isDigit() }
                        error = null
                    },
                    label = "Nominal Biaya (Rp)",
                    placeholder = "contoh: 150000"
                )

                HrisTextField(
                    value = description,
                    onValueChange = {
                        description = it
                        error = null
                    },
                    label = "Keterangan Pengeluaran",
                    placeholder = "contoh: Taksi dinas ke kantor cabang",
                    singleLine = false,
                    maxLines = 3
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Bg2)
                        .clickable { hasReceipt = !hasReceipt }
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = hasReceipt, onCheckedChange = { hasReceipt = it })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (hasReceipt) "Bukti struk / invoice terlampir" else "Tanpa struk (tidak disarankan)",
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
                text = "Kirim Klaim",
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    if (amount <= 0) {
                        error = "Nominal harus lebih dari 0"
                    } else if (description.isBlank()) {
                        error = "Keterangan klaim wajib diisi"
                    } else {
                        onSubmit(
                            CreateClaimRequest(
                                categoryId = selectedCatId,
                                claimDate = claimDate,
                                amount = amount,
                                description = description.trim(),
                                receipt = if (hasReceipt) "data:image/jpeg;base64,..." else null
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
