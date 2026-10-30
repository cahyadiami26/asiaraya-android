package id.co.asiaraya.hris.core.network

import id.co.asiaraya.hris.core.model.*
import retrofit2.Response
import retrofit2.http.*

interface HrisApiService {

    // Auth
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiEnvelope<LoginResponse>>

    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): Response<ApiEnvelope<LoginResponse>>

    @POST("auth/logout")
    suspend fun logout(): Response<ApiEnvelope<SimpleOk>>

    @GET("auth/me")
    suspend fun getMe(): Response<ApiEnvelope<MeResponse>>

    // Announcements
    @GET("announcements")
    suspend fun getAnnouncements(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("category") category: String? = null,
        @Query("q") query: String? = null,
        @Query("unread") unreadOnly: Boolean? = null
    ): Response<ApiEnvelope<List<Announcement>>>

    @GET("announcements/{id}")
    suspend fun getAnnouncementDetail(
        @Path("id") id: Long
    ): Response<ApiEnvelope<Announcement>>

    @POST("announcements/{id}/read")
    suspend fun markAnnouncementRead(
        @Path("id") id: Long
    ): Response<ApiEnvelope<SimpleOk>>

    // Schedule
    @GET("schedule/today")
    suspend fun getScheduleToday(): Response<ApiEnvelope<ScheduleTodayResponse>>

    @GET("schedule")
    suspend fun getScheduleRange(
        @Query("from") fromDate: String,
        @Query("to") toDate: String
    ): Response<ApiEnvelope<List<ScheduleDay>>>

    @GET("shifts")
    suspend fun getShifts(): Response<ApiEnvelope<List<Shift>>>

    @GET("schedule-requests")
    suspend fun getScheduleRequests(): Response<ApiEnvelope<List<ScheduleRequest>>>

    @POST("schedule-requests")
    suspend fun createScheduleRequest(
        @Body request: CreateScheduleRequest
    ): Response<ApiEnvelope<ScheduleRequest>>

    @POST("schedule-requests/{id}/cancel")
    suspend fun cancelScheduleRequest(
        @Path("id") id: Long
    ): Response<ApiEnvelope<SimpleOk>>

    // Attendance
    @GET("attendance/today")
    suspend fun getAttendanceToday(): Response<ApiEnvelope<AttendanceToday>>

    @POST("attendance/punch")
    suspend fun punchAttendance(
        @Header("Idempotency-Key") idempotencyKey: String,
        @Body request: PunchRequest
    ): Response<ApiEnvelope<PunchResponse>>

    @GET("attendance/history")
    suspend fun getAttendanceHistory(
        @Query("month") month: String
    ): Response<ApiEnvelope<List<AttendanceRecord>>>

    @GET("attendance/summary")
    suspend fun getAttendanceSummary(
        @Query("month") month: String
    ): Response<ApiEnvelope<AttendanceSummary>>

    // Overtime
    @GET("overtimes")
    suspend fun getOvertimes(
        @Query("month") month: String? = null,
        @Query("status") status: String? = null,
        @Query("per_page") perPage: Int = 20
    ): Response<ApiEnvelope<List<Overtime>>>

    @POST("overtimes")
    suspend fun createOvertime(
        @Body request: CreateOvertimeRequest
    ): Response<ApiEnvelope<Overtime>>

    @POST("overtimes/{id}/cancel")
    suspend fun cancelOvertime(
        @Path("id") id: Long
    ): Response<ApiEnvelope<SimpleOk>>

    // Leave
    @GET("leave/types")
    suspend fun getLeaveTypes(): Response<ApiEnvelope<List<LeaveType>>>

    @GET("leave/balances")
    suspend fun getLeaveBalances(
        @Query("year") year: Int? = null
    ): Response<ApiEnvelope<List<LeaveBalance>>>

    @GET("leaves")
    suspend fun getLeaves(
        @Query("status") status: String? = null,
        @Query("per_page") perPage: Int = 20
    ): Response<ApiEnvelope<List<LeaveItem>>>

    @POST("leaves")
    suspend fun createLeave(
        @Body request: CreateLeaveRequest
    ): Response<ApiEnvelope<LeaveItem>>

    @GET("leaves/{id}")
    suspend fun getLeaveDetail(
        @Path("id") id: Long
    ): Response<ApiEnvelope<LeaveItem>>

    @POST("leaves/{id}/cancel")
    suspend fun cancelLeave(
        @Path("id") id: Long
    ): Response<ApiEnvelope<SimpleOk>>

    // Payslips
    @GET("payslips")
    suspend fun getPayslips(
        @Query("year") year: Int? = null
    ): Response<ApiEnvelope<List<Payslip>>>

    @GET("payslips/{id}")
    suspend fun getPayslipDetail(
        @Path("id") id: Long
    ): Response<ApiEnvelope<Payslip>>

    // Claims
    @GET("claim-categories")
    suspend fun getClaimCategories(): Response<ApiEnvelope<List<ClaimCategory>>>

    @GET("claims")
    suspend fun getClaims(
        @Query("status") status: String? = null,
        @Query("per_page") perPage: Int = 20
    ): Response<ApiEnvelope<List<Claim>>>

    @POST("claims")
    suspend fun createClaim(
        @Body request: CreateClaimRequest
    ): Response<ApiEnvelope<Claim>>

    @GET("claims/{id}")
    suspend fun getClaimDetail(
        @Path("id") id: Long
    ): Response<ApiEnvelope<Claim>>

    @POST("claims/{id}/cancel")
    suspend fun cancelClaim(
        @Path("id") id: Long
    ): Response<ApiEnvelope<SimpleOk>>

    // Tax Slips
    @GET("tax-slips")
    suspend fun getTaxSlips(
        @Query("year") year: Int? = null
    ): Response<ApiEnvelope<List<TaxSlip>>>

    @GET("tax-slips/{year}")
    suspend fun getTaxSlipDetail(
        @Path("year") year: Int
    ): Response<ApiEnvelope<TaxSlip>>

    // Notifications
    @GET("notifications")
    suspend fun getNotifications(
        @Query("unread") unreadOnly: Boolean? = null,
        @Query("type") type: String? = null,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): Response<ApiEnvelope<List<Notification>>>

    @POST("notifications/read-all")
    suspend fun markAllNotificationsRead(): Response<ApiEnvelope<SimpleOk>>

    @POST("notifications/{id}/read")
    suspend fun markNotificationRead(
        @Path("id") id: Long
    ): Response<ApiEnvelope<SimpleOk>>

    // Approvals
    @GET("approvals")
    suspend fun getApprovals(
        @Query("type") type: String = "all",
        @Query("page") page: Int = 1
    ): Response<ApiEnvelope<List<ApprovalItem>>>

    @POST("approvals/{type}/{id}/decide")
    suspend fun decideApproval(
        @Path("type") type: String,
        @Path("id") id: Long,
        @Body request: ApprovalDecideRequest
    ): Response<ApiEnvelope<SimpleOk>>
}
