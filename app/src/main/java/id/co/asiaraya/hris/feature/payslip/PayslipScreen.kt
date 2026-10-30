package id.co.asiaraya.hris.feature.payslip

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.co.asiaraya.hris.core.model.Payslip
import id.co.asiaraya.hris.core.ui.components.*
import id.co.asiaraya.hris.core.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayslipScreen(
    payslips: List<Payslip>,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedPayslip by remember { mutableStateOf<Payslip?>(null) }

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
                Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Ink)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Slip Gaji Karyawan",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Navy900
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(
                    text = "Daftar Slip Gaji (24 Periode Terakhir)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Navy900
                )
            }

            if (payslips.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Default.ReceiptLong,
                        title = "Belum Ada Slip Gaji",
                        body = "Slip gaji bulanan Anda akan muncul di sini setelah diterbitkan oleh HR."
                    )
                }
            } else {
                items(payslips) { item ->
                    CardSurface(
                        modifier = Modifier.clickable { selectedPayslip = item }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = item.period?.label ?: "Oktober 2026",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Navy900
                                )
                                Text(
                                    text = item.slipNumber,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                    ),
                                    color = Muted
                                )
                            }
                            StatusBadge(status = item.period?.status ?: "PAID", customLabel = "Dibayarkan")
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Bg2)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Take Home Pay:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Ink2
                            )
                            Text(
                                text = Formatters.formatRupiah(item.takeHomePay),
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = Teal700
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Dibayarkan pada ${Formatters.formatDateShort(item.period?.payDate ?: "")}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = Muted
                            )
                            Text(
                                text = "Lihat Rincian →",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Teal700
                            )
                        }
                    }
                }
            }
        }
    }

    // Detail Bottom Sheet
    selectedPayslip?.let { slip ->
        ModalBottomSheet(
            onDismissRequest = { selectedPayslip = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = CardBg,
            shape = SheetShape
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = slip.period?.label ?: "Slip Gaji",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Navy900
                        )
                        Text(
                            text = slip.slipNumber,
                            style = MaterialTheme.typography.bodySmall,
                            color = Muted
                        )
                    }
                    StatusBadge(status = "PAID", customLabel = "Lunas")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Teal50)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Gaji Bersih Diterima:",
                            style = MaterialTheme.typography.titleSmall,
                            color = Teal700
                        )
                        Text(
                            text = Formatters.formatRupiah(slip.takeHomePay),
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = Teal700
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Rincian Komponen Gaji",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Navy900
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    slip.components.forEach { comp ->
                        val isEarning = comp.type == "EARNING"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = comp.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Ink2
                            )
                            Text(
                                text = (if (isEarning) "+ " else "- ") + Formatters.formatRupiah(comp.amount),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = if (isEarning) Teal700 else Rose600
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                TealButton(
                    text = "Unduh Slip PDF Resmi",
                    onClick = {
                        Toast.makeText(context, "Slip gaji PDF sedang diunduh...", Toast.LENGTH_SHORT).show()
                    },
                    icon = Icons.Default.Download,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlineButton(
                    text = "Tutup",
                    onClick = { selectedPayslip = null },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
