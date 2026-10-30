package id.co.asiaraya.hris.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.co.asiaraya.hris.core.model.*
import id.co.asiaraya.hris.core.ui.components.*
import id.co.asiaraya.hris.core.ui.theme.*

@Composable
fun HomeScreen(
    me: MeResponse?,
    announcements: List<Announcement>,
    onNavigateTab: (tabIndex: Int) -> Unit,
    onNavigateRoute: (route: String) -> Unit,
    onRefresh: () -> Unit
) {
    val employee = me?.employee
    val attendance = me?.attendanceToday
    val counters = me?.counters
    val canApprove = me?.permissions?.canApprove == true

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header Jumbotron Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CardShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Navy900, Navy850, Navy950)
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Selamat Datang,",
                                style = MaterialTheme.typography.bodySmall,
                                color = Teal400
                            )
                            Text(
                                text = employee?.fullName ?: "Karyawan",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = CardBg
                            )
                            Text(
                                text = "${employee?.position ?: "-"} · ${employee?.department ?: "-"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Muted2
                            )
                        }
                        EmployeeAvatar(
                            name = employee?.fullName ?: "AR",
                            photoUrl = employee?.photoUrl,
                            size = 50.dp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = Formatters.formatDateLong("2026-10-30"),
                        style = MaterialTheme.typography.bodySmall,
                        color = CardBg.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Attendance Card
        item {
            CardSurface {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Presensi Hari Ini",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Navy900
                        )
                        Text(
                            text = "${attendance?.shift?.name ?: "Shift Pagi"} (${attendance?.shiftStart ?: "08:00"} - ${attendance?.shiftEnd ?: "17:00"})",
                            style = MaterialTheme.typography.bodySmall,
                            color = Muted
                        )
                    }

                    val isCheckedIn = attendance?.attendance?.checkedIn == true
                    val statusText = if (isCheckedIn) "Hadir" else "Belum Presensi"
                    StatusBadge(status = if (isCheckedIn) "PRESENT" else "PENDING", customLabel = statusText)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Bg2)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "JAM MASUK",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = Muted
                        )
                        Text(
                            text = attendance?.attendance?.checkInAt?.let { Formatters.formatTime(it.takeLast(8)) } ?: "--:--",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Ink
                        )
                    }
                    Divider(
                        modifier = Modifier
                            .height(36.dp)
                            .width(1.dp),
                        color = LineStrong
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "JAM KELUAR",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = Muted
                        )
                        Text(
                            text = attendance?.attendance?.checkOutAt?.let { Formatters.formatTime(it.takeLast(8)) } ?: "--:--",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Ink
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                TealButton(
                    text = "Buka Presensi Online",
                    onClick = { onNavigateTab(1) }, // Tab 1 = Presensi
                    icon = Icons.Default.Fingerprint,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Approval Alert Card (if manager / hr / finance)
        if (canApprove && (counters?.approvalsPending ?: 0) > 0) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CardShape)
                        .background(Amber50)
                        .clickable { onNavigateRoute("approval") }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Amber600),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FactCheck,
                                contentDescription = null,
                                tint = CardBg,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Kotak Persetujuan (${counters?.approvalsPending} Menunggu)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Amber600
                            )
                            Text(
                                text = "Pengajuan bawahan siap ditinjau & diputuskan",
                                style = MaterialTheme.typography.bodySmall,
                                color = Ink2
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Amber600
                    )
                }
            }
        }

        // 4 KPI Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiTile(
                        label = "Hadir Bulan Ini",
                        value = "21 Hari",
                        icon = Icons.Default.CheckCircle,
                        modifier = Modifier.weight(1f)
                    )
                    KpiTile(
                        label = "Terlambat",
                        value = "1 Kali",
                        icon = Icons.Default.Schedule,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiTile(
                        label = "Sisa Cuti",
                        value = "9 Hari",
                        icon = Icons.Default.EventAvailable,
                        modifier = Modifier.weight(1f)
                    )
                    val openTotal = (counters?.leaveOpen ?: 0) + (counters?.overtimeOpen ?: 0) + (counters?.claimsOpen ?: 0)
                    KpiTile(
                        label = "Pengajuan Terbuka",
                        value = "$openTotal Aktif",
                        icon = Icons.Default.PendingActions,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Quick Shortcuts
        item {
            SectionCard(title = "Pintasan Layanan") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ShortcutItem(
                        icon = Icons.Default.ReceiptLong,
                        label = "Slip Gaji",
                        onClick = { onNavigateRoute("payslip") }
                    )
                    ShortcutItem(
                        icon = Icons.Default.MoreTime,
                        label = "Lembur",
                        onClick = { onNavigateRoute("overtime") }
                    )
                    ShortcutItem(
                        icon = Icons.Default.Payments,
                        label = "Klaim Biaya",
                        onClick = { onNavigateRoute("claim") }
                    )
                    ShortcutItem(
                        icon = Icons.Default.Calculate,
                        label = "Pajak 1721-A1",
                        onClick = { onNavigateRoute("tax") }
                    )
                }
            }
        }

        // Announcements
        item {
            SectionCard(
                title = "Pengumuman Terbaru",
                actionText = "Lihat Semua",
                onActionClick = { onNavigateRoute("announcement") }
            ) {
                if (announcements.isEmpty()) {
                    Text(
                        text = "Belum ada pengumuman baru.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Muted
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        announcements.take(3).forEach { ann ->
                            AnnouncementRow(ann) {
                                onNavigateRoute("announcement")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShortcutItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Teal50),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Teal700,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Medium),
            color = Ink2
        )
    }
}

@Composable
private fun AnnouncementRow(
    ann: Announcement,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Bg2)
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (ann.isPinned) {
            Text(text = "📌", modifier = Modifier.padding(end = 8.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = ann.title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Ink,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${ann.category} · ${Formatters.formatDateShort(ann.publishedAt)}",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = Muted
            )
        }
        if (ann.isUnread) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Teal600)
            )
        }
    }
}
