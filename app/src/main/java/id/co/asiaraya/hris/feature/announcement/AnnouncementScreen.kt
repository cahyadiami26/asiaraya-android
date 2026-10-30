package id.co.asiaraya.hris.feature.announcement

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import id.co.asiaraya.hris.core.model.Announcement
import id.co.asiaraya.hris.core.ui.components.*
import id.co.asiaraya.hris.core.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementScreen(
    announcements: List<Announcement>,
    onMarkRead: (Long) -> Unit,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedAnnouncement by remember { mutableStateOf<Announcement?>(null) }

    val filtered = announcements.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
                it.excerpt.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true)
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
                Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Ink)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Pengumuman Perusahaan",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Navy900
            )
        }

        // Search Bar
        Box(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Cari pengumuman...", style = MaterialTheme.typography.bodyMedium) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Muted) },
                singleLine = true,
                shape = InputShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Teal500,
                    unfocusedBorderColor = LineStrong,
                    focusedContainerColor = CardBg,
                    unfocusedContainerColor = CardBg
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (filtered.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Default.Campaign,
                        title = "Tidak Ada Pengumuman",
                        body = "Pengumuman yang Anda cari tidak ditemukan."
                    )
                }
            } else {
                items(filtered) { item ->
                    CardSurface(
                        modifier = Modifier.clickable {
                            selectedAnnouncement = item
                            onMarkRead(item.id)
                        }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (item.isPinned) {
                                    Text(text = "📌", modifier = Modifier.padding(end = 6.dp))
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(BadgeShape)
                                        .background(Teal50)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = item.category,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Teal700
                                    )
                                }
                            }
                            if (item.isUnread) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Teal600)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Navy900
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = item.excerpt,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Ink2,
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Diterbitkan oleh ${item.authorName ?: "HR Asia Raya"} · ${Formatters.formatDateShort(item.publishedAt)}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = Muted
                        )
                    }
                }
            }
        }
    }

    // Detail Bottom Sheet
    selectedAnnouncement?.let { ann ->
        ModalBottomSheet(
            onDismissRequest = { selectedAnnouncement = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = CardBg,
            shape = SheetShape
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (ann.isPinned) {
                        Text(text = "📌", modifier = Modifier.padding(end = 8.dp))
                    }
                    Box(
                        modifier = Modifier
                            .clip(BadgeShape)
                            .background(Teal50)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = ann.category,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Teal700
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = ann.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Navy900
                )

                Text(
                    text = "Oleh ${ann.authorName ?: "HR Team"} · ${Formatters.formatDateLong(ann.publishedAt)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Muted
                )

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Line)
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = ann.content,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Ink,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                PrimaryButton(
                    text = "Tutup",
                    onClick = { selectedAnnouncement = null },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
