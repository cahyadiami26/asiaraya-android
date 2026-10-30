package id.co.asiaraya.hris.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Employee(
    val id: Long = 0,
    @SerialName("employee_code") val employeeCode: String = "",
    @SerialName("full_name") val fullName: String = "",
    val email: String = "",
    val role: String = "KARYAWAN",
    val position: String = "",
    val department: String = "",
    @SerialName("business_unit") val businessUnit: String = "",
    val manager: Manager? = null,
    @SerialName("photo_url") val photoUrl: String? = null
)

@Serializable
data class Manager(
    val id: Long = 0,
    val name: String = ""
)

@Serializable
data class Tokens(
    @SerialName("token_type") val tokenType: String = "Bearer",
    @SerialName("access_token") val accessToken: String = "",
    @SerialName("expires_at") val expiresAt: String = "",
    @SerialName("expires_in") val expiresIn: Long = 28800,
    @SerialName("refresh_token") val refreshToken: String = "",
    @SerialName("refresh_expires_at") val refreshExpiresAt: String = ""
)

@Serializable
data class LoginResponse(
    val tokens: Tokens,
    val employee: Employee
)

@Serializable
data class LoginRequest(
    val identifier: String,
    val password: String,
    @SerialName("device_name") val deviceName: String = "Android Device",
    val platform: String = "android",
    @SerialName("app_version") val appVersion: String = "1.0.0"
)

@Serializable
data class RefreshTokenRequest(
    @SerialName("refresh_token") val refreshToken: String
)

@Serializable
data class MeResponse(
    val employee: Employee,
    val counters: Counters? = null,
    @SerialName("attendance_today") val attendanceToday: AttendanceToday? = null,
    val permissions: Permissions? = null
)

@Serializable
data class Counters(
    @SerialName("notifications_unread") val notificationsUnread: Int = 0,
    @SerialName("announcements_unread") val announcementsUnread: Int = 0,
    @SerialName("approvals_pending") val approvalsPending: Int = 0,
    @SerialName("leave_open") val leaveOpen: Int = 0,
    @SerialName("overtime_open") val overtimeOpen: Int = 0,
    @SerialName("claims_open") val claimsOpen: Int = 0
)

@Serializable
data class Permissions(
    @SerialName("can_approve") val canApprove: Boolean = false,
    val role: String = "KARYAWAN"
)

@Serializable
data class Shift(
    val id: Long = 0,
    val code: String = "",
    val name: String = "",
    val date: String? = null,
    @SerialName("start_time") val startTime: String = "08:00",
    @SerialName("end_time") val endTime: String = "17:00",
    @SerialName("break_minutes") val breakMinutes: Int = 60,
    @SerialName("late_tolerance") val lateTolerance: Int = 15,
    @SerialName("operational_type") val operationalType: String = "NORMAL"
)

@Serializable
data class Geofence(
    val required: Boolean = true,
    @SerialName("selfie_required") val selfieRequired: Boolean = true,
    val unit: String = "Jakarta HQ (Pusat)",
    val lat: Double = -6.2241,
    val lng: Double = 106.8089,
    val radius: Double = 50.0
)

@Serializable
data class AttendanceRecord(
    val id: Long = 0,
    val date: String = "",
    val status: String? = null,
    @SerialName("checked_in") val checkedIn: Boolean = false,
    @SerialName("checked_out") val checkedOut: Boolean = false,
    @SerialName("check_in_at") val checkInAt: String? = null,
    @SerialName("check_out_at") val checkOutAt: String? = null,
    @SerialName("check_in_photo_url") val checkInPhotoUrl: String? = null,
    @SerialName("late_minutes") val lateMinutes: Int = 0,
    @SerialName("work_minutes") val workMinutes: Int = 0,
    val note: String? = null
)

@Serializable
data class AttendanceToday(
    val date: String = "",
    val shift: Shift? = null,
    @SerialName("shift_start") val shiftStart: String = "08:00",
    @SerialName("shift_end") val shiftEnd: String = "17:00",
    @SerialName("late_tolerance") val lateTolerance: Int = 15,
    @SerialName("operational_type") val operationalType: String = "NORMAL",
    @SerialName("can_check_in") val canCheckIn: Boolean = true,
    @SerialName("can_check_out") val canCheckOut: Boolean = false,
    val attendance: AttendanceRecord? = null,
    val geofence: Geofence? = null
)

@Serializable
data class PunchRequest(
    val type: String, // in or out
    val lat: Double,
    val lng: Double,
    val accuracy: Double? = null,
    val photo: String? = null,
    val note: String? = null,
    val device: String? = "Android Device",
    @SerialName("captured_at") val capturedAt: String? = null,
    val offline: Boolean = false
)

@Serializable
data class PunchDetail(
    val type: String = "in",
    val status: String? = "PRESENT",
    @SerialName("late_minutes") val lateMinutes: Int = 0,
    @SerialName("work_minutes") val workMinutes: Int = 0,
    val distance: Double? = null,
    val geofence: String = "",
    val at: String = "",
    @SerialName("server_date") val serverDate: String? = null
)

@Serializable
data class PunchResponse(
    val punch: PunchDetail? = null,
    val attendance: AttendanceRecord? = null,
    val today: AttendanceToday? = null,
    val message: String = ""
)

@Serializable
data class AttendanceSummary(
    val month: String = "",
    val present: Int = 0,
    val late: Int = 0,
    val absent: Int = 0,
    @SerialName("late_minutes") val lateMinutes: Int = 0,
    @SerialName("work_hours") val workHours: Double = 0.0
)

@Serializable
data class ScheduleDay(
    val date: String = "",
    val dow: Int = 0,
    val weekend: Boolean = false,
    @SerialName("is_work") val isWork: Boolean = true,
    val shift: Shift? = null
)

@Serializable
data class ScheduleTodayResponse(
    val date: String = "",
    val shift: Shift? = null,
    @SerialName("next_days") val nextDays: List<ScheduleDay> = emptyList()
)

@Serializable
data class ScheduleRequest(
    val id: Long = 0,
    @SerialName("requested_shift") val requestedShift: Shift? = null,
    @SerialName("requested_shift_id") val requestedShiftId: Long? = null,
    @SerialName("start_date") val startDate: String = "",
    @SerialName("end_date") val endDate: String = "",
    val reason: String = "",
    val status: String = "PENDING",
    @SerialName("status_label") val statusLabel: String? = null,
    @SerialName("created_at") val createdAt: String = ""
)

@Serializable
data class CreateScheduleRequest(
    @SerialName("requested_shift_id") val requestedShiftId: Long,
    @SerialName("start_date") val startDate: String,
    @SerialName("end_date") val endDate: String,
    val reason: String = ""
)

@Serializable
data class LeaveType(
    val id: Long = 0,
    val code: String = "",
    val name: String = "",
    @SerialName("quota_days") val quotaDays: Double = 12.0,
    @SerialName("requires_attachment") val requiresAttachment: Boolean = false,
    @SerialName("max_per_request") val maxPerRequest: Int = 5,
    @SerialName("min_notice_days") val minNoticeDays: Int = 3
)

@Serializable
data class LeaveBalance(
    @SerialName("leave_type_id") val leaveTypeId: Long = 0,
    @SerialName("type_name") val typeName: String = "",
    @SerialName("type_code") val typeCode: String = "",
    val year: Int = 2026,
    val quota: Double = 0.0,
    @SerialName("carry_over") val carryOver: Double = 0.0,
    val used: Double = 0.0,
    val remaining: Double = 0.0,
    val unlimited: Boolean = false,
    val progress: Double = 0.0
)

@Serializable
data class LeaveItem(
    val id: Long = 0,
    @SerialName("leave_type") val leaveType: LeaveType? = null,
    @SerialName("start_date") val startDate: String = "",
    @SerialName("end_date") val endDate: String = "",
    val days: Double = 1.0,
    val reason: String = "",
    val status: String = "PENDING",
    @SerialName("status_label") val statusLabel: String? = null,
    @SerialName("attachment_url") val attachmentUrl: String? = null,
    @SerialName("created_at") val createdAt: String = "",
    val warnings: List<String> = emptyList()
)

@Serializable
data class CreateLeaveRequest(
    @SerialName("leave_type_id") val leaveTypeId: Long,
    @SerialName("start_date") val startDate: String,
    @SerialName("end_date") val endDate: String,
    val reason: String,
    val attachment: String? = null
)

@Serializable
data class Overtime(
    val id: Long = 0,
    @SerialName("work_date") val workDate: String = "",
    @SerialName("planned_start") val plannedStart: String = "",
    @SerialName("planned_end") val plannedEnd: String = "",
    @SerialName("actual_start") val actualStart: String? = null,
    @SerialName("actual_end") val actualEnd: String? = null,
    val hours: Double = 0.0,
    val compensation: Double = 0.0,
    val reason: String = "",
    val status: String = "PENDING",
    @SerialName("status_label") val statusLabel: String? = null,
    @SerialName("created_at") val createdAt: String = ""
)

@Serializable
data class CreateOvertimeRequest(
    @SerialName("work_date") val workDate: String,
    @SerialName("planned_start") val plannedStart: String,
    @SerialName("planned_end") val plannedEnd: String,
    val reason: String
)

@Serializable
data class ClaimCategory(
    val id: Long = 0,
    val code: String = "",
    val name: String = "",
    @SerialName("max_amount") val maxAmount: Double? = null,
    @SerialName("requires_receipt") val requiresReceipt: Boolean = true
)

@Serializable
data class Claim(
    val id: Long = 0,
    @SerialName("claim_number") val claimNumber: String = "",
    val category: ClaimCategory? = null,
    @SerialName("claim_date") val claimDate: String = "",
    val amount: Double = 0.0,
    val description: String = "",
    val status: String = "PENDING",
    @SerialName("status_label") val statusLabel: String? = null,
    @SerialName("receipt_url") val receiptUrl: String? = null,
    @SerialName("created_at") val createdAt: String = ""
)

@Serializable
data class CreateClaimRequest(
    @SerialName("category_id") val categoryId: Long,
    @SerialName("claim_date") val claimDate: String,
    val amount: Double,
    val description: String,
    val receipt: String? = null
)

@Serializable
data class PayslipPeriod(
    val year: Int = 2026,
    val month: Int = 10,
    val label: String = "Oktober 2026",
    @SerialName("pay_date") val payDate: String = "",
    val status: String = "PAID"
)

@Serializable
data class PayslipComponent(
    val name: String = "",
    val amount: Double = 0.0,
    val type: String = "EARNING" // EARNING or DEDUCTION
)

@Serializable
data class Payslip(
    val id: Long = 0,
    @SerialName("slip_number") val slipNumber: String = "",
    val period: PayslipPeriod? = null,
    @SerialName("business_unit") val businessUnit: String = "",
    @SerialName("gross_salary") val grossSalary: Double = 0.0,
    @SerialName("total_deduction") val totalDeduction: Double = 0.0,
    @SerialName("pph_amount") val pphAmount: Double = 0.0,
    @SerialName("net_salary") val netSalary: Double = 0.0,
    @SerialName("take_home_pay") val takeHomePay: Double = 0.0,
    @SerialName("download_url") val downloadUrl: String = "",
    val components: List<PayslipComponent> = emptyList()
)

@Serializable
data class TaxSlip(
    val year: Int = 2026,
    @SerialName("slip_number") val slipNumber: String = "",
    @SerialName("gross_income") val grossIncome: Double = 0.0,
    @SerialName("net_income") val netIncome: Double = 0.0,
    @SerialName("taxable_income") val taxableIncome: Double = 0.0,
    @SerialName("tax_payable") val taxPayable: Double = 0.0,
    @SerialName("tax_deducted") val taxDeducted: Double = 0.0,
    val status: String = "ISSUED",
    @SerialName("download_url") val downloadUrl: String = ""
)

@Serializable
data class Announcement(
    val id: Long = 0,
    val title: String = "",
    val category: String = "UMUM",
    val content: String = "",
    val excerpt: String = "",
    @SerialName("is_pinned") val isPinned: Boolean = false,
    @SerialName("is_unread") val isUnread: Boolean = false,
    @SerialName("published_at") val publishedAt: String = "",
    @SerialName("author_name") val authorName: String? = null
)

@Serializable
data class Notification(
    val id: Long = 0,
    val type: String = "INFO", // INFO, WARNING, SUCCESS, DANGER
    val title: String = "",
    val message: String = "",
    val link: String? = null,
    @SerialName("is_read") val isRead: Boolean = false,
    @SerialName("created_at") val createdAt: String = ""
)

@Serializable
data class ApprovalItem(
    val type: String = "", // leave, overtime, schedule, claim
    @SerialName("type_label") val typeLabel: String = "",
    val id: Long = 0,
    val employee: Employee? = null,
    val status: String = "PENDING",
    @SerialName("status_label") val statusLabel: String? = null,
    val reason: String? = null,
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("decide_url") val decideUrl: String = "",
    val detail: Map<String, String> = emptyMap()
)

@Serializable
data class ApprovalDecideRequest(
    val decision: String, // APPROVED or REJECTED
    val note: String? = null
)
