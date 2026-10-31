package id.co.asiaraya.hris.core.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import id.co.asiaraya.hris.core.model.ApiEnvelope
import id.co.asiaraya.hris.core.prefs.UserSessionStore
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.UUID
import java.util.concurrent.TimeUnit

class ApiClient(private val sessionStore: UserSessionStore) {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
        isLenient = true
    }

    private val refreshMutex = Mutex()

    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val token = runBlocking { sessionStore.getAccessToken() }

        val requestBuilder = originalRequest.newBuilder()
            .header("X-Request-Id", UUID.randomUUID().toString())
            .header("Accept", "application/json")

        if (!token.isNullOrBlank() && originalRequest.header("Authorization") == null) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        chain.proceed(requestBuilder.build())
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
        redactHeader("Authorization")
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .authenticator { _, response ->
            // Handle 401 refresh token flow
            if (responseCount(response) >= 2) {
                return@authenticator null // Do not retry more than once
            }

            runBlocking {
                refreshMutex.withLock {
                    val currentToken = sessionStore.getAccessToken()
                    val authHeader = response.request.header("Authorization")
                    // If token already updated by another coroutine
                    if (authHeader != null && authHeader != "Bearer $currentToken" && currentToken != null) {
                        return@withLock response.request.newBuilder()
                            .header("Authorization", "Bearer $currentToken")
                            .build()
                    }

                    val refreshToken = sessionStore.getRefreshToken()
                    if (refreshToken.isNullOrBlank()) {
                        sessionStore.clearSession()
                        return@withLock null
                    }

                    // Attempt refresh
                    val serverUrl = sessionStore.getServerUrl()
                    val refreshRequest = Request.Builder()
                        .url("$serverUrl/api/v1/auth/refresh")
                        .post("""{"refresh_token":"$refreshToken"}""".toRequestBody("application/json".toMediaType()))
                        .build()

                    val client = OkHttpClient()
                    val refreshResponse = client.newCall(refreshRequest).execute()
                    if (refreshResponse.isSuccessful) {
                        val body = refreshResponse.body?.string()
                        if (body != null) {
                            try {
                                val envelope = json.decodeFromString<ApiEnvelope<id.co.asiaraya.hris.core.model.LoginResponse>>(body)
                                envelope.data?.let { data ->
                                    sessionStore.saveSession(data.tokens, data.employee)
                                    return@withLock response.request.newBuilder()
                                        .header("Authorization", "Bearer ${data.tokens.accessToken}")
                                        .build()
                                }
                            } catch (e: Exception) {
                                sessionStore.clearSession()
                            }
                        }
                    } else {
                        sessionStore.clearSession()
                    }
                    null
                }
            }
        }
        .build()

    private var currentBaseUrl: String? = null
    private var currentService: HrisApiService? = null

    fun getService(): HrisApiService {
        val serverUrl = runBlocking { sessionStore.getServerUrl() }
        val baseUrl = if (serverUrl.endsWith("/")) "${serverUrl}api/v1/" else "$serverUrl/api/v1/"

        if (currentService == null || currentBaseUrl != baseUrl) {
            currentBaseUrl = baseUrl
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
            currentService = retrofit.create(HrisApiService::class.java)
        }
        return currentService!!
    }

    fun parseError(errorBody: String?): String {
        if (errorBody.isNullOrBlank()) return "Terjadi kesalahan pada server."
        return try {
            val envelope = json.decodeFromString<ApiEnvelope<Unit>>(errorBody)
            envelope.message ?: "Permintaan gagal diproses."
        } catch (e: Exception) {
            "Kesalahan: ${errorBody.take(120)}"
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
