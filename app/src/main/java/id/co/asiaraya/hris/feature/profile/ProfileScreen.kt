package id.co.asiaraya.hris.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import id.co.asiaraya.hris.core.model.Employee
import id.co.asiaraya.hris.core.ui.components.*
import id.co.asiaraya.hris.core.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    employee: Employee?,
    currentServerUrl: String,
    onSaveServerUrl: suspend (String) -> Unit,
    onLogout: suspend () -> Unit,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var showServerDialog by remember { mutableStateOf(false) }
    var showLogoutConfirm by remember { mutableStateOf(false) }

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
                text = "Profil & Pengaturan",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Navy900
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Employee Profile Card
            item {
                CardSurface {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        EmployeeAvatar(
                            name = employee?.fullName ?: "AR",
                            photoUrl = employee?.photoUrl,
                            size = 60.dp
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = employee?.fullName ?: "Karyawan",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Navy900
                            )
                            Text(
                                text = "NIK: ${employee?.employeeCode ?: "-"}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = Teal700
                            )
                            Text(
                                text = "${employee?.position ?: "-"} · ${employee?.department ?: "-"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Muted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Line)
                    Spacer(modifier = Modifier.height(16.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ProfileInfoRow("Unit Bisnis", employee?.businessUnit ?: "Jakarta HQ (Pusat)")
                        ProfileInfoRow("Email Resmi", employee?.email ?: "-")
                        ProfileInfoRow("Atasan Langsung", employee?.manager?.name ?: "Budi Santoso")
                        ProfileInfoRow("Peran Sistem", employee?.role ?: "KARYAWAN")
                    }
                }
            }

            // Server Configuration Card
            item {
                CardSurface {
                    Text(
                        text = "Pengaturan Server API",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Navy900
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tersambung ke: $currentServerUrl",
                        style = MaterialTheme.typography.bodySmall,
                        color = Teal700
                    )
                    Text(
                        text = "Backend: HRIS REST API v2.17.0 (WIB)",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = Muted
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SoftButton(
                            text = "Ubah Server",
                            onClick = { showServerDialog = true },
                            icon = Icons.Default.Dns,
                            modifier = Modifier.weight(1f)
                        )
                        OutlineButton(
                            text = "Bawaan",
                            onClick = {
                                coroutineScope.launch {
                                    onSaveServerUrl("https://hris.asiaraya.co.id")
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // About Application Card
            item {
                CardSurface {
                    Text(
                        text = "Tentang Aplikasi",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Navy900
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "HRIS Asia Raya Mobile · Versi 1.0.0 (Build 1)\nPT Asia Raya Group · All Rights Reserved.\nDikembangkan secara native dengan Kotlin & Jetpack Compose Material 3.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Ink2,
                        lineHeight = 18.sp
                    )
                }
            }

            // Logout Action
            item {
                DangerButton(
                    text = "Keluar dari Akun (Logout)",
                    onClick = { showLogoutConfirm = true },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if (showServerDialog) {
        var newUrl by remember { mutableStateOf(currentServerUrl) }
        AlertDialog(
            onDismissRequest = { showServerDialog = false },
            shape = DialogShape,
            containerColor = CardBg,
            title = {
                Text(
                    text = "Ganti Alamat Server",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Navy900
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Peringatan: Mengubah server akan mengakhiri sesi login aktif.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Rose600
                    )
                    HrisTextField(
                        value = newUrl,
                        onValueChange = { newUrl = it },
                        label = "URL Server HRIS",
                        placeholder = "https://hris.asiaraya.co.id"
                    )
                }
            },
            confirmButton = {
                TealButton(
                    text = "Simpan & Terapkan",
                    onClick = {
                        coroutineScope.launch {
                            onSaveServerUrl(newUrl)
                            showServerDialog = false
                        }
                    }
                )
            },
            dismissButton = {
                OutlineButton(
                    text = "Batal",
                    onClick = { showServerDialog = false }
                )
            }
        )
    }

    if (showLogoutConfirm) {
        ConfirmationDialog(
            title = "Keluar dari Aplikasi?",
            message = "Apakah Anda yakin ingin keluar? Token sesi dan data cache sementara akan dibersihkan.",
            confirmText = "Ya, Keluar",
            cancelText = "Batal",
            isDestructive = true,
            onConfirm = {
                coroutineScope.launch {
                    showLogoutConfirm = false
                    onLogout()
                }
            },
            onDismiss = { showLogoutConfirm = false }
        )
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Muted
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = Ink
        )
    }
}
