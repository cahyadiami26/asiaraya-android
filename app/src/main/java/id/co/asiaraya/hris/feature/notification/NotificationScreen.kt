package id.co.asiaraya.hris.feature.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.co.asiaraya.hris.core.model.Notification
import id.co.asiaraya.hris.core.ui.components.*
import id.co.asiaraya.hris.core.ui.theme.*

@Composable
fun NotificationScreen(
    notifications: List<Notification>,
    onMarkAllRead: () -> Unit,
    onMarkRead: (Long) -> Unit,
    onNavigateLink: (String) -> Unit,
    onBack: () -> Unit
) {
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = Ink)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Notifikasi",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Navy900
                )
            }
            TextButton(onClick = onMarkAllRead) {
                Text("Tandai Semua Dibaca", style = MaterialTheme.typography.labelMedium, color = Teal700)
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (notifications.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Default.NotificationsNone,
                        title = "Tidak Ada Notifikasi",
                        body = "Semua pembaruan aktivitas dan persetujuan akan muncul di sini."
                    )
                }
            } else {
                items(notifications) { notif ->
                    NotificationCard(
                        notif = notif,
                        onClick = {
                            onMarkRead(notif.id)
                            notif.link?.let { onNavigateLink(it) }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(
    notif: Notification,
    onClick: () -> Unit
) {
    val (icon, tint, bg) = when (notif.type) {
        "SUCCESS" -> Triple(Icons.Default.CheckCircle, Teal700, Teal50)
        "WARNING" -> Triple(Icons.Default.Warning, Amber600, Amber50)
        "DANGER" -> Triple(Icons.Default.Error, Rose600, Rose50)
        else -> Triple(Icons.Default.Info, Navy800, Bg2)
    }

    CardSurface(
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(bg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notif.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Ink
                    )
                    if (!notif.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Rose600)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notif.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ink2
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Hari ini · Baru saja",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = Muted
                )
            }
        }
    }
}
