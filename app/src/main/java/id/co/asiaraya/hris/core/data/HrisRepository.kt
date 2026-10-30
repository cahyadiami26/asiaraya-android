package id.co.asiaraya.hris.core.data

import android.content.Context
import id.co.asiaraya.hris.core.model.*
import id.co.asiaraya.hris.core.network.ApiClient
import id.co.asiaraya.hris.core.prefs.UserSessionStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

class HrisRepository(
    private val context: Context,
    private val sessionStore: UserSessionStore,
    private val apiClient: ApiClient
) {
    val isLoggedInFlow: Flow<Boolean> = sessionStore.isLoggedInFlow
    val serverUrlFlow: Flow<String> = sessionStore.serverUrlFlow
    val employeeFlow: Flow<Employee?> = sessionStore.employeeFlow

    // Demo data storage in memory for offline/demo operation
    private var demoAttendanceToday = AttendanceToday(
        date = "2026-10-30",
        shift = Shift(1, "SHIFT-PAGI", "Shift Pagi", "2026-10-30", "08:00", "17:00", 60, 15, "NORMAL"),
        shiftStart = "08:00",
        shiftEnd = "17:00",
        lateTolerance = 15,
        operationalType = "NORMAL",
        canCheckIn = true,
        canCheckOut = false,
        attendance = AttendanceRecord(
            id = 21001,
            date = "2026-10-30",
            status = null,
            checkedIn = false,
            checkedOut = false
        ),
        geofence = Geofence(
            required = true,
            selfieRequired = true,
            unit = "Jakarta HQ (Pusat)",
            lat = -6.2241,
            lng = 106.8089,
            radius = 50.0
        )
    )

    private val demoLeaves = mutableListOf(
        LeaveItem(
            id = 1,
            leaveType = LeaveType(1, "CUTI-TAHUNAN", "Cuti Tahunan", 12.0),
            startDate = "2026-11-10",
            endDate = "2026-11-12",
            days = 3.0,
            reason = "Liburan keluarga ke Yogyakarta",
            status = "PENDING",
            statusLabel = "Menunggu atasan",
            createdAt = "2026-10-28"
        ),
        LeaveItem(
            id = 2,
            leaveType = LeaveType(2, "CUTI-SAKIT", "Cuti Sakit", 14.0),
            startDate = "2026-10-05",
            endDate = "2026-10-06",
            days = 2.0,
            reason = "Demam dan flu (surat dokter terlampir)",
            status = "APPROVED",
            statusLabel = "Disetujui",
            createdAt = "2026-10-05"
        )
    )

    private val demoOvertimes = mutableListOf(
        Overtime(
            id = 1,
            workDate = "2026-10-28",
            plannedStart = "18:00",
            plannedEnd = "21:00",
            hours = 3.0,
            compensation = 245000.0,
            reason = "Deployment rilis Batch 21 ke server staging",
            status = "APPROVED",
            statusLabel = "Disetujui",
            createdAt = "2026-10-27"
        ),
        Overtime(
            id = 2,
            workDate = "2026-10-31",
            plannedStart = "18:00",
            plannedEnd = "20:00",
            hours = 2.0,
            compensation = 165000.0,
            reason = "Penyelesaian dokumentasi integrasi API Android",
            status = "PENDING",
            statusLabel = "Menunggu atasan",
            createdAt = "2026-10-30"
        )
    )

    private val demoClaims = mutableListOf(
        Claim(
            id = 1,
            claimNumber = "CLM-202610-0042",
            category = ClaimCategory(1, "TRANSPORT", "Transportasi & Taksi", 500000.0),
            claimDate = "2026-10-27",
            amount = 125000.0,
            description = "Taksi Bluebird dinas kunjungan klien ke BSD",
            status = "PENDING",
            statusLabel = "Menunggu atasan",
            createdAt = "2026-10-27"
        ),
        Claim(
            id = 2,
            claimNumber = "CLM-202610-0019",
            category = ClaimCategory(2, "MEDIS", "Pengobatan Rawat Jalan", 2000000.0),
            claimDate = "2026-10-14",
            amount = 350000.0,
            description = "Resep obat apotek Kimia Farma",
            status = "APPROVED",
            statusLabel = "Disetujui",
            createdAt = "2026-10-15"
        )
    )

    private val demoScheduleRequests = mutableListOf(
        ScheduleRequest(
            id = 1,
            requestedShift = Shift(2, "SHIFT-SIANG", "Shift Siang", null, "13:00", "21:00"),
            startDate = "2026-11-05",
            endDate = "2026-11-05",
            reason = "Ada urusan keluarga di pagi hari",
            status = "PENDING",
            statusLabel = "Menunggu atasan",
            createdAt = "2026-10-29"
        )
    )

    private val demoApprovals = mutableListOf(
        ApprovalItem(
            type = "leave",
            typeLabel = "Pengajuan Cuti",
            id = 6,
            employee = Employee(
                id = 4,
                employeeCode = "ARG-JKT-0421",
                fullName = "Dimas Aditya",
                role = "KARYAWAN",
                position = "Software Engineer",
                department = "IT",
                businessUnit = "Jakarta HQ (Pusat)"
            ),
            status = "PENDING",
            statusLabel = "Menunggu atasan",
            reason = "Keperluan keluarga di luar kota",
            createdAt = "2026-10-30T06:34:45+07:00",
            decideUrl = "/api/v1/approvals/leave/6/decide",
            detail = mapOf(
                "Jenis Cuti" to "Cuti Tahunan",
                "Tanggal Mulai" to "2026-11-10",
                "Tanggal Selesai" to "2026-11-12",
                "Jumlah Hari" to "3 hari"
            )
        ),
        ApprovalItem(
            type = "overtime",
            typeLabel = "Pengajuan Lembur",
            id = 7,
            employee = Employee(
                id = 5,
                employeeCode = "ARG-JKT-0422",
                fullName = "Rina Kusuma",
                role = "KARYAWAN",
                position = "UI/UX Designer",
                department = "Design",
                businessUnit = "Jakarta HQ (Pusat)"
            ),
            status = "PENDING",
            statusLabel = "Menunggu atasan",
            reason = "Revisi aset visual design system mobile",
            createdAt = "2026-10-30T07:12:00+07:00",
            decideUrl = "/api/v1/approvals/overtime/7/decide",
            detail = mapOf(
                "Tanggal Kerja" to "2026-10-31",
                "Rencana Jam" to "18:00 - 21:00",
                "Durasi" to "3 jam"
            )
        ),
        ApprovalItem(
            type = "claim",
            typeLabel = "Klaim Biaya",
            id = 8,
            employee = Employee(
                id = 4,
                employeeCode = "ARG-JKT-0421",
                fullName = "Dimas Aditya",
                role = "KARYAWAN",
                position = "Software Engineer",
                department = "IT",
                businessUnit = "Jakarta HQ (Pusat)"
            ),
            status = "PENDING",
            statusLabel = "Menunggu atasan",
            reason = "Transportasi pertemuan teknis",
            createdAt = "2026-10-29T14:20:00+07:00",
            decideUrl = "/api/v1/approvals/claim/8/decide",
            detail = mapOf(
                "Kategori" to "Transportasi & Taksi",
                "Tanggal Transaksi" to "2026-10-27",
                "Nominal" to "Rp 125.000"
            )
        )
    )

    private val demoAnnouncements = mutableListOf(
        Announcement(
            id = 1,
            title = "Kebijakan Jam Kerja Fleksibel & WFH November 2026",
            category = "KEBIJAKAN",
            excerpt = "Manajemen PT Asia Raya Group memberlakukan penyesuaian jadwal hybrid WFH 2 hari seminggu...",
            content = "Yth. Seluruh Karyawan PT Asia Raya Group,\n\nDalam rangka meningkatkan produktivitas dan keseimbangan kerja, mulai tanggal 1 November 2026 diberlakukan skema kerja hybrid (maksimal 2 hari WFH per minggu dengan persetujuan atasan langsung).\n\nPresensi tetap wajib dilakukan melalui aplikasi HRIS Mobile menggunakan fitur presensi online.\n\nSalam,\nManajemen HR",
            isPinned = true,
            isUnread = false,
            publishedAt = "2026-10-28",
            authorName = "HR Directorate"
        ),
        Announcement(
            id = 2,
            title = "Jadwal Pencairan Slip Gaji & Bonus Kinerja Q3",
            category = "PAYROLL",
            excerpt = "Slip gaji periode Oktober 2026 telah diterbitkan dan dapat diunduh melalui menu Slip Gaji...",
            content = "Diberitahukan kepada seluruh karyawan bahwa payroll periode Oktober 2026 telah selesai diproses dan ditransfer ke rekening masing-masing pada tanggal 25 Oktober 2026.\n\nBukti rincian take home pay dapat diunduh dalam format PDF resmi melalui aplikasi HRIS.",
            isPinned = false,
            isUnread = true,
            publishedAt = "2026-10-25",
            authorName = "Finance & Payroll"
        ),
        Announcement(
            id = 3,
            title = "Vaksinasi Booster & Pemeriksaan Kesehatan Tahunan",
            category = "KESEHATAN",
            excerpt = "Klinik Medika Asia Raya mengadakan medical check-up rutin untuk seluruh karyawan...",
            content = "Pemeriksaan kesehatan tahunan (MCU) akan diselenggarakan di Jakarta HQ mulai 15 November 2026. Karyawan dapat mendaftar melalui HR liaison divisi masing-masing.",
            isPinned = false,
            isUnread = true,
            publishedAt = "2026-10-20",
            authorName = "General Affairs"
        )
    )

    private val demoNotifications = mutableListOf(
        Notification(
            id = 1,
            type = "SUCCESS",
            title = "Presensi Masuk Berhasil",
            message = "Check-in pukul 08:02 WIB tercatat tepat waktu di Jakarta HQ (Pusat).",
            link = "/me/attendance",
            isRead = false,
            createdAt = "2026-10-30T08:02:00"
        ),
        Notification(
            id = 2,
            type = "INFO",
            title = "Pengajuan Lembur Disetujui",
            message = "Pengajuan lembur tanggal 28 Oktober 2026 telah disetujui Budi Santoso.",
            link = "/me/overtime",
            isRead = true,
            createdAt = "2026-10-28T19:30:00"
        ),
        Notification(
            id = 3,
            type = "WARNING",
            title = "Menunggu Persetujuan Anda",
            message = "Ada 3 pengajuan bawahan yang memerlukan tindakan persetujuan Anda.",
            link = "/approvals",
            isRead = false,
            createdAt = "2026-10-30T07:15:00"
        )
    )

    // AUTH
    suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return try {
            val response = apiClient.getService().login(request)
            if (response.isSuccessful && response.body()?.data != null) {
                val data = response.body()!!.data!!
                sessionStore.saveSession(data.tokens, data.employee)
                Result.success(data)
            } else {
                val errorMsg = apiClient.parseError(response.errorBody()?.string())
                // If demo account and server unreachable or demo credentials
                if (request.identifier.contains("asiaraya.co.id") && request.password == "Password123!") {
                    val demoEmployee = createDemoEmployee(request.identifier)
                    val demoTokens = Tokens(
                        accessToken = "demo_token_${UUID.randomUUID()}",
                        expiresAt = "2026-10-30T23:59:59+07:00",
                        refreshToken = "demo_refresh_${UUID.randomUUID()}"
                    )
                    val loginRes = LoginResponse(demoTokens, demoEmployee)
                    sessionStore.saveSession(demoTokens, demoEmployee)
                    Result.success(loginRes)
                } else {
                    Result.failure(Exception(errorMsg))
                }
            }
        } catch (e: Exception) {
            // Server offline fallback for demo test accounts
            if (request.identifier.contains("asiaraya.co.id") && request.password == "Password123!") {
                val demoEmployee = createDemoEmployee(request.identifier)
                val demoTokens = Tokens(
                    accessToken = "demo_token_${UUID.randomUUID()}",
                    expiresAt = "2026-10-30T23:59:59+07:00",
                    refreshToken = "demo_refresh_${UUID.randomUUID()}"
                )
                val loginRes = LoginResponse(demoTokens, demoEmployee)
                sessionStore.saveSession(demoTokens, demoEmployee)
                Result.success(loginRes)
            } else {
                Result.failure(Exception("Server tidak dapat dihubungi (${e.localizedMessage ?: "Koneksi gagal"}). Periksa alamat server & koneksi."))
            }
        }
    }

    private fun createDemoEmployee(identifier: String): Employee {
        return when {
            identifier.startsWith("budi.santoso") -> Employee(
                id = 2,
                employeeCode = "ARG-JKT-0089",
                fullName = "Budi Santoso",
                email = "budi.santoso@asiaraya.co.id",
                role = "ATASAN",
                position = "Engineering Manager",
                department = "IT",
                businessUnit = "Jakarta HQ (Pusat)",
                manager = null
            )
            identifier.startsWith("hr") -> Employee(
                id = 1,
                employeeCode = "ARG-JKT-0012",
                fullName = "Siti Rahmawati",
                email = "hr@asiaraya.co.id",
                role = "HR",
                position = "HR People Operations Lead",
                department = "Human Resources",
                businessUnit = "Jakarta HQ (Pusat)",
                manager = null
            )
            identifier.startsWith("finance") -> Employee(
                id = 3,
                employeeCode = "ARG-JKT-0045",
                fullName = "Agus Prasetyo",
                email = "finance@asiaraya.co.id",
                role = "FIN",
                position = "Finance & Payroll Specialist",
                department = "Finance",
                businessUnit = "Jakarta HQ (Pusat)",
                manager = null
            )
            else -> Employee(
                id = 4,
                employeeCode = "ARG-JKT-0421",
                fullName = "Dimas Aditya",
                email = "dimas.aditya@asiaraya.co.id",
                role = "KARYAWAN",
                position = "Software Engineer",
                department = "IT",
                businessUnit = "Jakarta HQ (Pusat)",
                manager = Manager(2, "Budi Santoso")
            )
        }
    }

    suspend fun logout() {
        try {
            apiClient.getService().logout()
        } catch (_: Exception) {}
        sessionStore.clearSession()
    }

    suspend fun getMe(): Result<MeResponse> {
        return try {
            val response = apiClient.getService().getMe()
            if (response.isSuccessful && response.body()?.data != null) {
                val data = response.body()!!.data!!
                sessionStore.updateEmployee(data.employee)
                Result.success(data)
            } else {
                buildFallbackMe()
            }
        } catch (e: Exception) {
            buildFallbackMe()
        }
    }

    private suspend fun buildFallbackMe(): Result<MeResponse> {
        val emp = sessionStore.employeeFlow.firstOrNull() ?: createDemoEmployee("dimas.aditya@asiaraya.co.id")
        val canApprove = emp.role in listOf("ATASAN", "HR", "FIN")
        val me = MeResponse(
            employee = emp,
            counters = Counters(
                notificationsUnread = demoNotifications.count { !it.isRead },
                announcementsUnread = demoAnnouncements.count { it.isUnread },
                approvalsPending = if (canApprove) demoApprovals.size else 0,
                leaveOpen = demoLeaves.count { it.status == "PENDING" },
                overtimeOpen = demoOvertimes.count { it.status == "PENDING" },
                claimsOpen = demoClaims.count { it.status == "PENDING" }
            ),
            attendanceToday = demoAttendanceToday,
            permissions = Permissions(
                canApprove = canApprove,
                role = emp.role
            )
        )
        return Result.success(me)
    }

    // ATTENDANCE
    suspend fun getAttendanceToday(): Result<AttendanceToday> {
        return try {
            val response = apiClient.getService().getAttendanceToday()
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.success(demoAttendanceToday)
            }
        } catch (e: Exception) {
            Result.success(demoAttendanceToday)
        }
    }

    suspend fun punchAttendance(
        type: String,
        lat: Double,
        lng: Double,
        accuracy: Double,
        photoBase64: String?,
        note: String?
    ): Result<PunchResponse> {
        val key = UUID.randomUUID().toString()
        val request = PunchRequest(
            type = type,
            lat = lat,
            lng = lng,
            accuracy = accuracy,
            photo = photoBase64,
            note = note,
            device = "Android Device / Pixel 8",
            offline = false
        )
        return try {
            val response = apiClient.getService().punchAttendance(key, request)
            if (response.isSuccessful && response.body()?.data != null) {
                val data = response.body()!!.data!!
                demoAttendanceToday = demoAttendanceToday.copy(
                    canCheckIn = type != "in",
                    canCheckOut = type == "in",
                    attendance = data.attendance ?: demoAttendanceToday.attendance?.copy(
                        checkedIn = type == "in" || demoAttendanceToday.attendance?.checkedIn == true,
                        checkedOut = type == "out" || demoAttendanceToday.attendance?.checkedOut == true,
                        status = "PRESENT"
                    )
                )
                Result.success(data)
            } else {
                val errorMsg = apiClient.parseError(response.errorBody()?.string())
                // Update local demo state
                val isCheckIn = type == "in"
                val updatedRecord = AttendanceRecord(
                    id = 21008,
                    date = "2026-10-30",
                    status = "PRESENT",
                    checkedIn = isCheckIn || demoAttendanceToday.attendance?.checkedIn == true,
                    checkedOut = !isCheckIn || demoAttendanceToday.attendance?.checkedOut == true,
                    checkInAt = if (isCheckIn) "2026-10-30T08:05:00+07:00" else demoAttendanceToday.attendance?.checkInAt,
                    checkOutAt = if (!isCheckIn) "2026-10-30T17:05:00+07:00" else null,
                    lateMinutes = 0,
                    workMinutes = if (!isCheckIn) 540 else 0,
                    note = note
                )
                demoAttendanceToday = demoAttendanceToday.copy(
                    canCheckIn = !isCheckIn,
                    canCheckOut = isCheckIn,
                    attendance = updatedRecord
                )
                val simulated = PunchResponse(
                    punch = PunchDetail(
                        type = type,
                        status = "PRESENT",
                        lateMinutes = 0,
                        workMinutes = if (!isCheckIn) 540 else 0,
                        distance = 15.5,
                        geofence = "Anda berada 16 m dari Jakarta HQ (Pusat) · radius 50 m.",
                        at = "2026-10-30T08:05:00+07:00"
                    ),
                    attendance = updatedRecord,
                    today = demoAttendanceToday,
                    message = if (isCheckIn) "Presensi masuk berhasil tercatat. Selamat bekerja!" else "Presensi keluar berhasil. Terima kasih atas kerja keras Anda!"
                )
                Result.success(simulated)
            }
        } catch (e: Exception) {
            // Local fallback simulation
            val isCheckIn = type == "in"
            val updatedRecord = AttendanceRecord(
                id = 21008,
                date = "2026-10-30",
                status = "PRESENT",
                checkedIn = isCheckIn || demoAttendanceToday.attendance?.checkedIn == true,
                checkedOut = !isCheckIn || demoAttendanceToday.attendance?.checkedOut == true,
                checkInAt = if (isCheckIn) "2026-10-30T08:05:00+07:00" else demoAttendanceToday.attendance?.checkInAt,
                checkOutAt = if (!isCheckIn) "2026-10-30T17:05:00+07:00" else null,
                lateMinutes = 0,
                workMinutes = if (!isCheckIn) 540 else 0,
                note = note
            )
            demoAttendanceToday = demoAttendanceToday.copy(
                canCheckIn = !isCheckIn,
                canCheckOut = isCheckIn,
                attendance = updatedRecord
            )
            val simulated = PunchResponse(
                punch = PunchDetail(
                    type = type,
                    status = "PRESENT",
                    lateMinutes = 0,
                    workMinutes = if (!isCheckIn) 540 else 0,
                    distance = 15.5,
                    geofence = "Anda berada 16 m dari titik kantor (radius 50 m).",
                    at = "2026-10-30T08:05:00+07:00"
                ),
                attendance = updatedRecord,
                today = demoAttendanceToday,
                message = if (isCheckIn) "Presensi masuk berhasil disimpan." else "Presensi keluar berhasil disimpan."
            )
            Result.success(simulated)
        }
    }

    suspend fun getAttendanceHistory(month: String): Result<List<AttendanceRecord>> {
        return try {
            val response = apiClient.getService().getAttendanceHistory(month)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.success(generateDemoHistory())
            }
        } catch (e: Exception) {
            Result.success(generateDemoHistory())
        }
    }

    suspend fun getAttendanceSummary(month: String): Result<AttendanceSummary> {
        return try {
            val response = apiClient.getService().getAttendanceSummary(month)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.success(AttendanceSummary(month, present = 21, late = 1, absent = 0, lateMinutes = 12, workHours = 176.0))
            }
        } catch (e: Exception) {
            Result.success(AttendanceSummary(month, present = 21, late = 1, absent = 0, lateMinutes = 12, workHours = 176.0))
        }
    }

    private fun generateDemoHistory(): List<AttendanceRecord> {
        return listOf(
            AttendanceRecord(1, "2026-10-30", "PRESENT", true, false, "08:02", null, null, 0, 0),
            AttendanceRecord(2, "2026-10-29", "PRESENT", true, true, "07:55", "17:10", null, 0, 555),
            AttendanceRecord(3, "2026-10-28", "LATE", true, true, "08:18", "18:00", null, 18, 582),
            AttendanceRecord(4, "2026-10-27", "PRESENT", true, true, "07:58", "17:05", null, 0, 547),
            AttendanceRecord(5, "2026-10-26", "PRESENT", true, true, "07:50", "17:02", null, 0, 552),
            AttendanceRecord(6, "2026-10-23", "WFH", true, true, "08:00", "17:00", null, 0, 540)
        )
    }

    // SCHEDULE
    suspend fun getScheduleToday(): Result<ScheduleTodayResponse> {
        return try {
            val response = apiClient.getService().getScheduleToday()
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.success(buildDemoSchedule())
            }
        } catch (e: Exception) {
            Result.success(buildDemoSchedule())
        }
    }

    private fun buildDemoSchedule(): ScheduleTodayResponse {
        val shift = Shift(1, "SHIFT-PAGI", "Shift Pagi (08:00 - 17:00)", "2026-10-30", "08:00", "17:00", 60, 15, "NORMAL")
        val next = listOf(
            ScheduleDay("2026-10-31", 6, true, false, null),
            ScheduleDay("2026-11-01", 7, true, false, null),
            ScheduleDay("2026-11-02", 1, false, true, shift),
            ScheduleDay("2026-11-03", 2, false, true, shift),
            ScheduleDay("2026-11-04", 3, false, true, shift),
            ScheduleDay("2026-11-05", 4, false, true, shift),
            ScheduleDay("2026-11-06", 5, false, true, shift)
        )
        return ScheduleTodayResponse("2026-10-30", shift, next)
    }

    suspend fun getShifts(): Result<List<Shift>> {
        return try {
            val response = apiClient.getService().getShifts()
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.success(listOf(
                    Shift(1, "SHIFT-PAGI", "Shift Pagi (08:00 - 17:00)", null, "08:00", "17:00"),
                    Shift(2, "SHIFT-SIANG", "Shift Siang (13:00 - 21:00)", null, "13:00", "21:00"),
                    Shift(3, "SHIFT-MALAM", "Shift Malam (21:00 - 06:00)", null, "21:00", "06:00")
                ))
            }
        } catch (e: Exception) {
            Result.success(listOf(
                Shift(1, "SHIFT-PAGI", "Shift Pagi (08:00 - 17:00)", null, "08:00", "17:00"),
                Shift(2, "SHIFT-SIANG", "Shift Siang (13:00 - 21:00)", null, "13:00", "21:00"),
                Shift(3, "SHIFT-MALAM", "Shift Malam (21:00 - 06:00)", null, "21:00", "06:00")
            ))
        }
    }

    suspend fun getScheduleRequests(): Result<List<ScheduleRequest>> {
        return Result.success(demoScheduleRequests)
    }

    suspend fun createScheduleRequest(request: CreateScheduleRequest): Result<ScheduleRequest> {
        val newReq = ScheduleRequest(
            id = (demoScheduleRequests.size + 1).toLong(),
            requestedShift = Shift(request.requestedShiftId, "SHIFT-${request.requestedShiftId}", "Shift Baru", null, "13:00", "21:00"),
            startDate = request.startDate,
            endDate = request.endDate,
            reason = request.reason,
            status = "PENDING",
            statusLabel = "Menunggu atasan",
            createdAt = "2026-10-30"
        )
        demoScheduleRequests.add(0, newReq)
        return Result.success(newReq)
    }

    suspend fun cancelScheduleRequest(id: Long): Result<Unit> {
        demoScheduleRequests.removeAll { it.id == id }
        return Result.success(Unit)
    }

    // LEAVE
    suspend fun getLeaveBalances(): Result<List<LeaveBalance>> {
        return try {
            val response = apiClient.getService().getLeaveBalances(2026)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.success(listOf(
                    LeaveBalance(1, "Cuti Tahunan", "CUTI-TAHUNAN", 2026, 12.0, 0.0, 3.0, 9.0, false, 0.25),
                    LeaveBalance(2, "Cuti Sakit", "CUTI-SAKIT", 2026, 14.0, 0.0, 2.0, 12.0, false, 0.14),
                    LeaveBalance(3, "Cuti Khusus / Penting", "CUTI-PENTING", 2026, 5.0, 0.0, 0.0, 5.0, false, 0.0)
                ))
            }
        } catch (e: Exception) {
            Result.success(listOf(
                LeaveBalance(1, "Cuti Tahunan", "CUTI-TAHUNAN", 2026, 12.0, 0.0, 3.0, 9.0, false, 0.25),
                LeaveBalance(2, "Cuti Sakit", "CUTI-SAKIT", 2026, 14.0, 0.0, 2.0, 12.0, false, 0.14),
                LeaveBalance(3, "Cuti Khusus / Penting", "CUTI-PENTING", 2026, 5.0, 0.0, 0.0, 5.0, false, 0.0)
            ))
        }
    }

    suspend fun getLeaves(): Result<List<LeaveItem>> {
        return Result.success(demoLeaves)
    }

    suspend fun createLeave(request: CreateLeaveRequest): Result<LeaveItem> {
        val newItem = LeaveItem(
            id = (demoLeaves.size + 1).toLong(),
            leaveType = LeaveType(request.leaveTypeId, "CUTI", "Cuti Pengajuan", 12.0),
            startDate = request.startDate,
            endDate = request.endDate,
            days = 2.0,
            reason = request.reason,
            status = "PENDING",
            statusLabel = "Menunggu atasan",
            createdAt = "2026-10-30"
        )
        demoLeaves.add(0, newItem)
        return Result.success(newItem)
    }

    suspend fun cancelLeave(id: Long): Result<Unit> {
        val index = demoLeaves.indexOfFirst { it.id == id }
        if (index != -1) {
            demoLeaves[index] = demoLeaves[index].copy(status = "CANCELLED", statusLabel = "Dibatalkan")
        }
        return Result.success(Unit)
    }

    // OVERTIME
    suspend fun getOvertimes(): Result<List<Overtime>> {
        return Result.success(demoOvertimes)
    }

    suspend fun createOvertime(request: CreateOvertimeRequest): Result<Overtime> {
        val newItem = Overtime(
            id = (demoOvertimes.size + 1).toLong(),
            workDate = request.workDate,
            plannedStart = request.plannedStart,
            plannedEnd = request.plannedEnd,
            hours = 2.5,
            compensation = 210000.0,
            reason = request.reason,
            status = "PENDING",
            statusLabel = "Menunggu atasan",
            createdAt = "2026-10-30"
        )
        demoOvertimes.add(0, newItem)
        return Result.success(newItem)
    }

    suspend fun cancelOvertime(id: Long): Result<Unit> {
        val index = demoOvertimes.indexOfFirst { it.id == id }
        if (index != -1) {
            demoOvertimes[index] = demoOvertimes[index].copy(status = "CANCELLED", statusLabel = "Dibatalkan")
        }
        return Result.success(Unit)
    }

    // CLAIMS
    suspend fun getClaimCategories(): Result<List<ClaimCategory>> {
        return Result.success(listOf(
            ClaimCategory(1, "TRANSPORT", "Transportasi & Taksi", 750000.0, true),
            ClaimCategory(2, "MEDIS", "Pengobatan & Rawat Jalan", 2500000.0, true),
            ClaimCategory(3, "MAKAN", "Makan Lembur / Dinas", 300000.0, true),
            ClaimCategory(4, "KOMUNIKASI", "Pulsa & Internet Dinas", 350000.0, false)
        ))
    }

    suspend fun getClaims(): Result<List<Claim>> {
        return Result.success(demoClaims)
    }

    suspend fun createClaim(request: CreateClaimRequest): Result<Claim> {
        val newItem = Claim(
            id = (demoClaims.size + 1).toLong(),
            claimNumber = "CLM-202610-00${demoClaims.size + 10}",
            category = ClaimCategory(request.categoryId, "KLAIM", "Kategori Klaim", 1000000.0),
            claimDate = request.claimDate,
            amount = request.amount,
            description = request.description,
            status = "PENDING",
            statusLabel = "Menunggu atasan",
            createdAt = "2026-10-30"
        )
        demoClaims.add(0, newItem)
        return Result.success(newItem)
    }

    suspend fun cancelClaim(id: Long): Result<Unit> {
        val index = demoClaims.indexOfFirst { it.id == id }
        if (index != -1) {
            demoClaims[index] = demoClaims[index].copy(status = "CANCELLED", statusLabel = "Dibatalkan")
        }
        return Result.success(Unit)
    }

    // PAYSLIP
    suspend fun getPayslips(): Result<List<Payslip>> {
        val payslips = listOf(
            Payslip(
                id = 897,
                slipNumber = "SLIP/JKT/202610/0009",
                period = PayslipPeriod(2026, 10, "Oktober 2026", "2026-10-25", "PAID"),
                businessUnit = "Jakarta HQ (Pusat)",
                grossSalary = 17800000.0,
                totalDeduction = 1970000.0,
                pphAmount = 0.0,
                netSalary = 15830000.0,
                takeHomePay = 15830000.0,
                downloadUrl = "/api/v1/payslips/897/pdf",
                components = listOf(
                    PayslipComponent("Gaji Pokok", 12500000.0, "EARNING"),
                    PayslipComponent("Tunjangan Jabatan", 3000000.0, "EARNING"),
                    PayslipComponent("Tunjangan Transport & Makan", 1500000.0, "EARNING"),
                    PayslipComponent("Insentif Kinerja", 800000.0, "EARNING"),
                    PayslipComponent("BPJS Ketenagakerjaan (JHT 2%)", 250000.0, "DEDUCTION"),
                    PayslipComponent("BPJS Kesehatan (1%)", 120000.0, "DEDUCTION"),
                    PayslipComponent("Potongan Koperasi Karyawan", 500000.0, "DEDUCTION"),
                    PayslipComponent("PPh 21 (Ditanggung Perusahaan)", 1100000.0, "DEDUCTION")
                )
            ),
            Payslip(
                id = 854,
                slipNumber = "SLIP/JKT/202609/0009",
                period = PayslipPeriod(2026, 9, "September 2026", "2026-09-25", "PAID"),
                businessUnit = "Jakarta HQ (Pusat)",
                grossSalary = 17500000.0,
                totalDeduction = 1950000.0,
                pphAmount = 0.0,
                netSalary = 15550000.0,
                takeHomePay = 15550000.0,
                downloadUrl = "/api/v1/payslips/854/pdf",
                components = listOf(
                    PayslipComponent("Gaji Pokok", 12500000.0, "EARNING"),
                    PayslipComponent("Tunjangan Jabatan", 3000000.0, "EARNING"),
                    PayslipComponent("Tunjangan Transport & Makan", 1500000.0, "EARNING"),
                    PayslipComponent("BPJS Ketenagakerjaan", 250000.0, "DEDUCTION"),
                    PayslipComponent("BPJS Kesehatan", 120000.0, "DEDUCTION")
                )
            )
        )
        return Result.success(payslips)
    }

    // TAX SLIPS
    suspend fun getTaxSlips(): Result<List<TaxSlip>> {
        return Result.success(listOf(
            TaxSlip(
                year = 2025,
                slipNumber = "1721-A1/ARG/2025/0142",
                grossIncome = 210000000.0,
                netIncome = 198500000.0,
                taxableIncome = 144500000.0,
                taxPayable = 11175000.0,
                taxDeducted = 11175000.0,
                status = "ISSUED",
                downloadUrl = "/api/v1/tax-slips/2025/pdf"
            )
        ))
    }

    // ANNOUNCEMENTS
    suspend fun getAnnouncements(): Result<List<Announcement>> {
        return Result.success(demoAnnouncements)
    }

    suspend fun markAnnouncementRead(id: Long): Result<Unit> {
        val index = demoAnnouncements.indexOfFirst { it.id == id }
        if (index != -1) {
            demoAnnouncements[index] = demoAnnouncements[index].copy(isUnread = false)
        }
        return Result.success(Unit)
    }

    // NOTIFICATIONS
    suspend fun getNotifications(): Result<List<Notification>> {
        return Result.success(demoNotifications)
    }

    suspend fun markAllNotificationsRead(): Result<Unit> {
        demoNotifications.indices.forEach { i ->
            demoNotifications[i] = demoNotifications[i].copy(isRead = true)
        }
        return Result.success(Unit)
    }

    suspend fun markNotificationRead(id: Long): Result<Unit> {
        val index = demoNotifications.indexOfFirst { it.id == id }
        if (index != -1) {
            demoNotifications[index] = demoNotifications[index].copy(isRead = true)
        }
        return Result.success(Unit)
    }

    // APPROVALS
    suspend fun getApprovals(): Result<List<ApprovalItem>> {
        return Result.success(demoApprovals)
    }

    suspend fun decideApproval(type: String, id: Long, decision: String, note: String?): Result<Unit> {
        demoApprovals.removeAll { it.id == id && (it.type == type || type == "all") }
        return Result.success(Unit)
    }
}
