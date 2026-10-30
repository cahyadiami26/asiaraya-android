package id.co.asiaraya.hris.feature.attendance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.co.asiaraya.hris.core.model.*
import id.co.asiaraya.hris.core.ui.components.*
import id.co.asiaraya.hris.core.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AttendanceScreen(
    attendanceToday: AttendanceToday?,
    onPunch: suspend (type: String, lat: Double, lng: Double, accuracy: Double, photo: String?, note: String?) -> Result<PunchResponse>,
    onNavigateHistory: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var currentTime by remember { mutableStateOf("08:00:00") }

    // Live clock timer
    LaunchedEffect(Unit) {
        val format = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        while (true) {
            currentTime = format.format(Date())
            delay(1000)
        }
    }

    var selfieTaken by remember { mutableStateOf(true) }
    var note by remember { mutableStateOf("") }
    var isPunching by remember { mutableStateOf(false) }
    var punchResult by remember { mutableStateOf<PunchResponse?>(null) }
    var punchError by remember { mutableStateOf<String?>(null) }

    val checkedIn = attendanceToday?.attendance?.checkedIn == true
    val checkedOut = attendanceToday?.attendance?.checkedOut == true

    val statusChipText = when {
        checkedOut -> "Presensi Selesai"
        checkedIn -> "Sedang Bekerja"
        else -> "Belum Presensi"
    }

    val statusChipColor = when {
        checkedOut -> Slate600
        checkedIn -> Teal700
        else -> Amber600
    }

    val statusChipBg = when {
        checkedOut -> Slate50
        checkedIn -> Teal50
        else -> Amber50
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Digital Clock Card
        CardSurface {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .clip(BadgeShape)
                        .background(statusChipBg)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = statusChipText,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = statusChipColor
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = currentTime,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp
                    ),
                    color = Navy900
                )

                Text(
                    text = Formatters.formatDateLong("2026-10-30"),
                    style = MaterialTheme.typography.bodySmall,
                    color = Muted
                )
            }
        }

        // Camera Preview & Selfie Card
        CardSurface {
            Text(
                text = "Foto Selfie Presensi",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Navy900
            )
            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Navy950)
                    .border(1.dp, Line, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (selfieTaken) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Teal600),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Face,
                                contentDescription = null,
                                tint = CardBg,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Selfie Kamera Depan Terverifikasi",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = Teal400
                        )
                        Text(
                            text = "Resolusi 720 px · Siap dikirim",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = Muted2
                        )
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = CardBg,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Ketuk untuk mengambil selfie",
                            style = MaterialTheme.typography.bodySmall,
                            color = CardBg
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Selfie wajib (aturan kantor)",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = Muted
                )
                Text(
                    text = if (selfieTaken) "Ulangi Foto" else "Ambil Foto",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Teal700,
                    modifier = Modifier.clickable { selfieTaken = !selfieTaken }
                )
            }
        }

        // GPS Geofence Location Card
        CardSurface {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Teal50),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Teal700,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Lokasi Terverifikasi",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Ink
                    )
                    Text(
                        text = "Anda berada ±16 m dari Jakarta HQ (Pusat) · radius 50 m",
                        style = MaterialTheme.typography.bodySmall,
                        color = Teal700
                    )
                    Text(
                        text = "Koordinat: -6.2241, 106.8089 (Akurasi GPS 8 m)",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = Muted
                    )
                }
            }
        }

        // Optional Note Input
        HrisTextField(
            value = note,
            onValueChange = { note = it },
            label = "Catatan Presensi (Opsional)",
            placeholder = "contoh: Tugas meeting klien di lantai 8",
            testTag = "punch_note_input"
        )

        // Error message if any
        if (punchError != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Rose50, RoundedCornerShape(8.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = Rose600,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = punchError!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = Rose600
                )
            }
        }

        // Success message banner if punch result returned
        if (punchResult != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Teal50),
                border = androidx.compose.foundation.BorderStroke(1.dp, Teal100),
                shape = CardShape
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Teal700,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = punchResult!!.message,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Teal700
                        )
                    }
                    punchResult!!.punch?.let { p ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = p.geofence,
                            style = MaterialTheme.typography.bodySmall,
                            color = Ink2
                        )
                    }
                }
            }
        }

        // Punch Actions: Masuk & Keluar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PrimaryButton(
                text = "Presensi Masuk",
                onClick = {
                    coroutineScope.launch {
                        isPunching = true
                        punchError = null
                        val result = onPunch("in", -6.2241, 106.8089, 8.0, "data:image/jpeg;base64,...", note)
                        isPunching = false
                        result.onSuccess {
                            punchResult = it
                        }.onFailure {
                            punchError = it.message ?: "Presensi gagal"
                        }
                    }
                },
                enabled = !checkedIn && !isPunching,
                loading = isPunching,
                icon = Icons.Default.Login,
                modifier = Modifier.weight(1f),
                testTag = "punch_in_button"
            )

            TealButton(
                text = "Presensi Keluar",
                onClick = {
                    coroutineScope.launch {
                        isPunching = true
                        punchError = null
                        val result = onPunch("out", -6.2241, 106.8089, 8.0, "data:image/jpeg;base64,...", note)
                        isPunching = false
                        result.onSuccess {
                            punchResult = it
                        }.onFailure {
                            punchError = it.message ?: "Presensi keluar gagal"
                        }
                    }
                },
                enabled = checkedIn && !checkedOut && !isPunching,
                loading = isPunching,
                icon = Icons.Default.Logout,
                modifier = Modifier.weight(1f),
                testTag = "punch_out_button"
            )
        }

        // View History CTA
        OutlineButton(
            text = "Lihat Riwayat & Rekap Presensi",
            onClick = onNavigateHistory,
            icon = Icons.Default.History,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}
