package id.co.asiaraya.hris.feature.tax

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.co.asiaraya.hris.core.model.TaxSlip
import id.co.asiaraya.hris.core.ui.components.*
import id.co.asiaraya.hris.core.ui.theme.*

@Composable
fun TaxSlipScreen(
    slips: List<TaxSlip>,
    onBack: () -> Unit
) {
    val context = LocalContext.current

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
                text = "Bukti Potong Pajak 1721-A1",
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
                    text = "Bukti Pemotongan PPh Pasal 21",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Navy900
                )
            }

            if (slips.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Default.Description,
                        title = "Bukti Potong Belum Diterbitkan",
                        body = "Bukti potong 1721-A1 tahun pajak berjalan belum diterbitkan oleh bagian HR/Finance."
                    )
                }
            } else {
                items(slips) { slip ->
                    CardSurface {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Tahun Pajak ${slip.year}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Navy900
                                )
                                Text(
                                    text = slip.slipNumber,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                    ),
                                    color = Muted
                                )
                            }
                            StatusBadge(status = "APPROVED", customLabel = "Diterbitkan")
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Bg2)
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            TaxRow("Penghasilan Bruto", Formatters.formatRupiah(slip.grossIncome))
                            TaxRow("Penghasilan Neto", Formatters.formatRupiah(slip.netIncome))
                            TaxRow("Penghasilan Kena Pajak (PKP)", Formatters.formatRupiah(slip.taxableIncome))
                            TaxRow("PPh Pasal 21 Terutang", Formatters.formatRupiah(slip.taxPayable), isHighlight = true)
                            TaxRow("PPh Pasal 21 Telah Dipotong", Formatters.formatRupiah(slip.taxDeducted), isHighlight = true)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        TealButton(
                            text = "Unduh Berkas 1721-A1 (PDF)",
                            onClick = {
                                Toast.makeText(context, "Mengunduh Bukti Potong 1721-A1 PDF...", Toast.LENGTH_SHORT).show()
                            },
                            icon = Icons.Default.Download,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TaxRow(label: String, value: String, isHighlight: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Ink2
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (isHighlight) Teal700 else Ink
        )
    }
}
