package id.co.asiaraya.hris.feature.shell

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import id.co.asiaraya.hris.feature.tax.TaxSlipScreen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.co.asiaraya.hris.core.data.HrisRepository
import id.co.asiaraya.hris.core.model.*
import id.co.asiaraya.hris.core.ui.components.*
import id.co.asiaraya.hris.core.ui.theme.*
import id.co.asiaraya.hris.feature.announcement.AnnouncementScreen
import id.co.asiaraya.hris.feature.approval.ApprovalScreen
import id.co.asiaraya.hris.feature.attendance.AttendanceHistoryScreen
import id.co.asiaraya.hris.feature.attendance.AttendanceScreen
import id.co.asiaraya.hris.feature.claim.ClaimScreen
import id.co.asiaraya.hris.feature.home.HomeScreen
import id.co.asiaraya.hris.feature.leave.LeaveScreen
import id.co.asiaraya.hris.feature.notification.NotificationScreen
import id.co.asiaraya.hris.feature.overtime.OvertimeScreen
import id.co.asiaraya.hris.feature.payslip.PayslipScreen
import id.co.asiaraya.hris.feature.profile.ProfileScreen
import id.co.asiaraya.hris.feature.schedule.ScheduleScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShellScreen(
    repository: HrisRepository,
    onLogoutDone: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var activeTab by remember { mutableIntStateOf(0) } // 0: Home, 1: Attendance, 2: Schedule, 3: Leave
    var currentSubRoute by remember { mutableStateOf<String?>(null) } // payslip, overtime, claim, tax, announcement, notification, approval, profile, attendance_history
    var showMoreSheet by remember { mutableStateOf(false) }

    // Live data states
    var me by remember { mutableStateOf<MeResponse?>(null) }
    var attendanceToday by remember { mutableStateOf<AttendanceToday?>(null) }
    var attendanceSummary by remember { mutableStateOf<AttendanceSummary?>(null) }
    var attendanceHistory by remember { mutableStateOf<List<AttendanceRecord>>(emptyList()) }
    var scheduleToday by remember { mutableStateOf<ScheduleTodayResponse?>(null) }
    var shifts by remember { mutableStateOf<List<Shift>>(emptyList()) }
    var scheduleRequests by remember { mutableStateOf<List<ScheduleRequest>>(emptyList()) }
    var leaveBalances by remember { mutableStateOf<List<LeaveBalance>>(emptyList()) }
    var leaves by remember { mutableStateOf<List<LeaveItem>>(emptyList()) }
    var overtimes by remember { mutableStateOf<List<Overtime>>(emptyList()) }
    var claims by remember { mutableStateOf<List<Claim>>(emptyList()) }
    var claimCategories by remember { mutableStateOf<List<ClaimCategory>>(emptyList()) }
    var payslips by remember { mutableStateOf<List<Payslip>>(emptyList()) }
    var taxSlips by remember { mutableStateOf<List<TaxSlip>>(emptyList()) }
    var announcements by remember { mutableStateOf<List<Announcement>>(emptyList()) }
    var notifications by remember { mutableStateOf<List<Notification>>(emptyList()) }
    var approvals by remember { mutableStateOf<List<ApprovalItem>>(emptyList()) }
    val serverUrl by repository.serverUrlFlow.collectAsState(initial = "https://hris.asiaraya.co.id")

    // Load initial data
    LaunchedEffect(Unit) {
        repository.getMe().onSuccess { me = it }
        repository.getAttendanceToday().onSuccess { attendanceToday = it }
        repository.getAttendanceSummary("2026-10").onSuccess { attendanceSummary = it }
        repository.getAttendanceHistory("2026-10").onSuccess { attendanceHistory = it }
        repository.getScheduleToday().onSuccess { scheduleToday = it }
        repository.getShifts().onSuccess { shifts = it }
        repository.getScheduleRequests().onSuccess { scheduleRequests = it }
        repository.getLeaveBalances().onSuccess { leaveBalances = it }
        repository.getLeaves().onSuccess { leaves = it }
        repository.getOvertimes().onSuccess { overtimes = it }
        repository.getClaimCategories().onSuccess { claimCategories = it }
        repository.getClaims().onSuccess { claims = it }
        repository.getPayslips().onSuccess { payslips = it }
        repository.getTaxSlips().onSuccess { taxSlips = it }
        repository.getAnnouncements().onSuccess { announcements = it }
        repository.getNotifications().onSuccess { notifications = it }
        repository.getApprovals().onSuccess { approvals = it }
    }

    // Hardware back press handler
    BackHandler(enabled = currentSubRoute != null || activeTab != 0) {
        if (currentSubRoute != null) {
            currentSubRoute = null
        } else {
            activeTab = 0
        }
    }

    Scaffold(
        containerColor = Bg,
        topBar = {
            if (currentSubRoute == null) {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "HRIS Asia Raya",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = CardBg
                            )
                            Text(
                                text = "PT Asia Raya Group",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = Teal400
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Navy900,
                        titleContentColor = CardBg,
                        actionIconContentColor = CardBg
                    ),
                    actions = {
                        // Unread notification bell with badge
                        val unreadNotif = notifications.count { !it.isRead }
                        Box(modifier = Modifier.padding(end = 4.dp)) {
                            IconButton(onClick = { currentSubRoute = "notification" }) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifikasi",
                                    tint = CardBg
                                )
                            }
                            if (unreadNotif > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Rose600)
                                        .align(Alignment.TopEnd)
                                        .offset(x = (-8).dp, y = 8.dp)
                                )
                            }
                        }

                        // Avatar
                        IconButton(onClick = { currentSubRoute = "profile" }) {
                            EmployeeAvatar(
                                name = me?.employee?.fullName ?: "AR",
                                photoUrl = me?.employee?.photoUrl,
                                size = 32.dp
                            )
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (currentSubRoute == null) {
                NavigationBar(
                    containerColor = CardBg,
                    contentColor = Navy900,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Beranda") },
                        label = { Text("Beranda") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Teal700,
                            selectedTextColor = Teal700,
                            indicatorColor = Teal50
                        )
                    )
                    NavigationBarItem(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        icon = { Icon(Icons.Default.Fingerprint, contentDescription = "Presensi") },
                        label = { Text("Presensi") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Teal700,
                            selectedTextColor = Teal700,
                            indicatorColor = Teal50
                        )
                    )
                    NavigationBarItem(
                        selected = activeTab == 2,
                        onClick = { activeTab = 2 },
                        icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Jadwal") },
                        label = { Text("Jadwal") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Teal700,
                            selectedTextColor = Teal700,
                            indicatorColor = Teal50
                        )
                    )
                    NavigationBarItem(
                        selected = activeTab == 3,
                        onClick = { activeTab = 3 },
                        icon = { Icon(Icons.Default.EventNote, contentDescription = "Cuti") },
                        label = { Text("Cuti") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Teal700,
                            selectedTextColor = Teal700,
                            indicatorColor = Teal50
                        )
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { showMoreSheet = true },
                        icon = {
                            val canApprove = me?.permissions?.canApprove == true
                            val pendingCount = approvals.size
                            BadgedBox(
                                badge = {
                                    if (canApprove && pendingCount > 0) {
                                        Badge(containerColor = Amber600) {
                                            Text("$pendingCount")
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Menu, contentDescription = "Lainnya")
                            }
                        },
                        label = { Text("Lainnya") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Teal700,
                            selectedTextColor = Teal700,
                            indicatorColor = Teal50
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentSubRoute) {
                "payslip" -> PayslipScreen(
                    payslips = payslips,
                    onBack = { currentSubRoute = null }
                )
                "overtime" -> OvertimeScreen(
                    overtimes = overtimes,
                    onCreateOvertime = { req ->
                        val res = repository.createOvertime(req)
                        res.onSuccess { repository.getOvertimes().onSuccess { overtimes = it } }
                        res
                    },
                    onCancelOvertime = { id ->
                        val res = repository.cancelOvertime(id)
                        res.onSuccess { repository.getOvertimes().onSuccess { overtimes = it } }
                        res
                    },
                    onBack = { currentSubRoute = null }
                )
                "claim" -> ClaimScreen(
                    claims = claims,
                    categories = claimCategories,
                    onCreateClaim = { req ->
                        val res = repository.createClaim(req)
                        res.onSuccess { repository.getClaims().onSuccess { claims = it } }
                        res
                    },
                    onCancelClaim = { id ->
                        val res = repository.cancelClaim(id)
                        res.onSuccess { repository.getClaims().onSuccess { claims = it } }
                        res
                    },
                    onBack = { currentSubRoute = null }
                )
                "tax" -> TaxSlipScreen(
                    slips = taxSlips,
                    onBack = { currentSubRoute = null }
                )
                "announcement" -> AnnouncementScreen(
                    announcements = announcements,
                    onMarkRead = { id ->
                        coroutineScope.launch {
                            repository.markAnnouncementRead(id)
                            repository.getAnnouncements().onSuccess { announcements = it }
                        }
                    },
                    onBack = { currentSubRoute = null }
                )
                "notification" -> NotificationScreen(
                    notifications = notifications,
                    onMarkAllRead = {
                        coroutineScope.launch {
                            repository.markAllNotificationsRead()
                            repository.getNotifications().onSuccess { notifications = it }
                        }
                    },
                    onMarkRead = { id ->
                        coroutineScope.launch {
                            repository.markNotificationRead(id)
                            repository.getNotifications().onSuccess { notifications = it }
                        }
                    },
                    onNavigateLink = { link ->
                        currentSubRoute = when {
                            link.contains("attendance") -> { activeTab = 1; null }
                            link.contains("schedule") -> { activeTab = 2; null }
                            link.contains("leave") -> { activeTab = 3; null }
                            link.contains("overtime") -> "overtime"
                            link.contains("payslip") -> "payslip"
                            link.contains("approval") -> "approval"
                            else -> null
                        }
                    },
                    onBack = { currentSubRoute = null }
                )
                "approval" -> ApprovalScreen(
                    approvals = approvals,
                    onDecide = { type, id, dec, note ->
                        val res = repository.decideApproval(type, id, dec, note)
                        res.onSuccess { repository.getApprovals().onSuccess { approvals = it } }
                        res
                    },
                    onBack = { currentSubRoute = null }
                )
                "attendance_history" -> AttendanceHistoryScreen(
                    summary = attendanceSummary,
                    records = attendanceHistory,
                    onBack = { currentSubRoute = null }
                )
                "profile" -> ProfileScreen(
                    employee = me?.employee,
                    currentServerUrl = serverUrl,
                    onSaveServerUrl = { newUrl ->
                        repository.logout()
                        onLogoutDone()
                    },
                    onLogout = {
                        repository.logout()
                        onLogoutDone()
                    },
                    onBack = { currentSubRoute = null }
                )
                else -> {
                    when (activeTab) {
                        0 -> HomeScreen(
                            me = me,
                            announcements = announcements,
                            onNavigateTab = { tab -> activeTab = tab },
                            onNavigateRoute = { route -> currentSubRoute = route },
                            onRefresh = {
                                coroutineScope.launch {
                                    repository.getMe().onSuccess { me = it }
                                    repository.getAnnouncements().onSuccess { announcements = it }
                                }
                            }
                        )
                        1 -> AttendanceScreen(
                            attendanceToday = attendanceToday,
                            onPunch = { type, lat, lng, acc, photo, note ->
                                val res = repository.punchAttendance(type, lat, lng, acc, photo, note)
                                res.onSuccess {
                                    repository.getAttendanceToday().onSuccess { attendanceToday = it }
                                    repository.getMe().onSuccess { me = it }
                                }
                                res
                            },
                            onNavigateHistory = { currentSubRoute = "attendance_history" }
                        )
                        2 -> ScheduleScreen(
                            scheduleToday = scheduleToday,
                            availableShifts = shifts,
                            requests = scheduleRequests,
                            onCreateRequest = { req ->
                                val res = repository.createScheduleRequest(req)
                                res.onSuccess { repository.getScheduleRequests().onSuccess { scheduleRequests = it } }
                                res
                            },
                            onCancelRequest = { id ->
                                val res = repository.cancelScheduleRequest(id)
                                res.onSuccess { repository.getScheduleRequests().onSuccess { scheduleRequests = it } }
                                res
                            }
                        )
                        3 -> LeaveScreen(
                            balances = leaveBalances,
                            leaves = leaves,
                            onCreateLeave = { req ->
                                val res = repository.createLeave(req)
                                res.onSuccess {
                                    repository.getLeaves().onSuccess { leaves = it }
                                    repository.getLeaveBalances().onSuccess { leaveBalances = it }
                                }
                                res
                            },
                            onCancelLeave = { id ->
                                val res = repository.cancelLeave(id)
                                res.onSuccess { repository.getLeaves().onSuccess { leaves = it } }
                                res
                            }
                        )
                    }
                }
            }
        }
    }

    // "Lainnya" More Menu BottomSheet
    if (showMoreSheet) {
        ModalBottomSheet(
            onDismissRequest = { showMoreSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = CardBg,
            shape = SheetShape
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Menu & Layanan Karyawan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Navy900
                )
                Spacer(modifier = Modifier.height(16.dp))

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    MoreMenuItem(Icons.Default.ReceiptLong, "Slip Gaji", "Rincian take home pay & unduh PDF") {
                        showMoreSheet = false
                        currentSubRoute = "payslip"
                    }
                    MoreMenuItem(Icons.Default.MoreTime, "Lembur", "Pengajuan jam kerja lembur & kompensasi") {
                        showMoreSheet = false
                        currentSubRoute = "overtime"
                    }
                    MoreMenuItem(Icons.Default.Payments, "Klaim Biaya", "Reimbursement operasional & medis") {
                        showMoreSheet = false
                        currentSubRoute = "claim"
                    }
                    MoreMenuItem(Icons.Default.Calculate, "Pajak 1721-A1", "Bukti potong pajak tahunan resmi") {
                        showMoreSheet = false
                        currentSubRoute = "tax"
                    }
                    MoreMenuItem(Icons.Default.Campaign, "Pengumuman", "Informasi resmi manajemen perusahaan") {
                        showMoreSheet = false
                        currentSubRoute = "announcement"
                    }
                    MoreMenuItem(Icons.Default.Notifications, "Notifikasi", "Aktivitas dan persetujuan terkini") {
                        showMoreSheet = false
                        currentSubRoute = "notification"
                    }

                    if (me?.permissions?.canApprove == true) {
                        MoreMenuItem(
                            Icons.Default.FactCheck,
                            "Kotak Persetujuan",
                            "Setujui/tolak cuti, lembur, tukar shift, klaim",
                            badgeText = "${approvals.size} baru"
                        ) {
                            showMoreSheet = false
                            currentSubRoute = "approval"
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = Line)

                    MoreMenuItem(Icons.Default.Person, "Profil & Pengaturan", "Data akun, ubah server, dan info aplikasi") {
                        showMoreSheet = false
                        currentSubRoute = "profile"
                    }
                }
            }
        }
    }
}

@Composable
private fun MoreMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    badgeText: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Teal50),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Teal700, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = Ink
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Muted
            )
        }
        if (badgeText != null) {
            Box(
                modifier = Modifier
                    .clip(BadgeShape)
                    .background(Amber50)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = badgeText,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Amber600
                )
            }
        } else {
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Muted2)
        }
    }
}
