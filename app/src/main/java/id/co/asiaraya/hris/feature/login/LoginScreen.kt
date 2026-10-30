package id.co.asiaraya.hris.feature.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.co.asiaraya.hris.R
import id.co.asiaraya.hris.core.model.LoginRequest
import id.co.asiaraya.hris.core.ui.components.*
import id.co.asiaraya.hris.core.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    currentServerUrl: String,
    onSaveServerUrl: suspend (String) -> Unit,
    onLoginSubmit: suspend (LoginRequest) -> Result<Unit>,
    onLoginSuccess: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var serverUrl by remember(currentServerUrl) { mutableStateOf(currentServerUrl) }
    var showServerConfig by remember { mutableStateOf(false) }

    var identifier by remember { mutableStateOf("dimas.aditya@asiaraya.co.id") }
    var password by remember { mutableStateOf("Password123!") }
    var passwordVisible by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var infoMessage by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // App Logo
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Navy900),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.hris_launcher_icon_1793357965887),
                contentDescription = "Logo HRIS",
                modifier = Modifier.size(56.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "HRIS Asia Raya",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Navy900
        )

        Text(
            text = "Masuk ke portal layanan karyawan",
            style = MaterialTheme.typography.bodyMedium,
            color = Muted
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Main Login Card
        CardSurface {
            // Expandable Server Config Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showServerConfig = !showServerConfig }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Dns,
                        contentDescription = null,
                        tint = Teal700,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Server: $serverUrl",
                        style = MaterialTheme.typography.bodySmall,
                        color = Ink2,
                        maxLines = 1
                    )
                }
                Icon(
                    imageVector = if (showServerConfig) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Muted,
                    modifier = Modifier.size(18.dp)
                )
            }

            AnimatedVisibility(visible = showServerConfig) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 12.dp)
                        .background(Bg2, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    HrisTextField(
                        value = serverUrl,
                        onValueChange = { serverUrl = it },
                        label = "Alamat Server",
                        placeholder = "https://hris.asiaraya.co.id",
                        testTag = "server_url_input"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SoftButton(
                            text = "Simpan",
                            onClick = {
                                coroutineScope.launch {
                                    onSaveServerUrl(serverUrl)
                                    infoMessage = "Alamat server diperbarui"
                                    showServerConfig = false
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                        OutlineButton(
                            text = "Bawaan",
                            onClick = {
                                serverUrl = "https://hris.asiaraya.co.id"
                                coroutineScope.launch {
                                    onSaveServerUrl(serverUrl)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Identifier Input
            HrisTextField(
                value = identifier,
                onValueChange = {
                    identifier = it
                    errorMessage = null
                },
                label = "Email / NIK / Username",
                placeholder = "contoh: dimas.aditya@asiaraya.co.id",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Muted
                    )
                },
                testTag = "identifier_input"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Input
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Kata Sandi",
                    style = MaterialTheme.typography.labelMedium,
                    color = Ink2,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = null
                    },
                    placeholder = {
                        Text(
                            text = "Masukkan kata sandi",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Muted2
                        )
                    },
                    singleLine = true,
                    shape = InputShape,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (!isLoading) {
                                coroutineScope.launch {
                                    isLoading = true
                                    val result = onLoginSubmit(
                                        LoginRequest(identifier.trim(), password)
                                    )
                                    isLoading = false
                                    result.onSuccess { onLoginSuccess() }
                                        .onFailure { errorMessage = it.message }
                                }
                            }
                        }
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Muted
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (passwordVisible) "Sembunyikan sandi" else "Tampilkan sandi",
                                tint = Muted
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Teal500,
                        unfocusedBorderColor = LineStrong,
                        errorBorderColor = Rose600,
                        focusedContainerColor = CardBg,
                        unfocusedContainerColor = CardBg
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("password_input")
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Rose50, RoundedCornerShape(8.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = Rose600,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = errorMessage!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = Rose600
                    )
                }
            }

            if (infoMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = infoMessage!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = Teal700
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            PrimaryButton(
                text = "Masuk",
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        errorMessage = null
                        val result = onLoginSubmit(
                            LoginRequest(identifier.trim(), password)
                        )
                        isLoading = false
                        result.onSuccess {
                            onLoginSuccess()
                        }.onFailure {
                            errorMessage = it.message ?: "Login gagal. Periksa data Anda."
                        }
                    }
                },
                loading = isLoading,
                modifier = Modifier.fillMaxWidth(),
                testTag = "login_submit_button"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Demo Accounts Quick Picker
        CardSurface {
            Text(
                text = "Akun Demo Cepat (Pilih untuk menguji):",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Muted
            )
            Spacer(modifier = Modifier.height(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DemoAccountRow("Dimas Aditya (Karyawan)", "dimas.aditya@asiaraya.co.id") {
                    identifier = "dimas.aditya@asiaraya.co.id"
                    password = "Password123!"
                }
                DemoAccountRow("Budi Santoso (Atasan/Mgr)", "budi.santoso@asiaraya.co.id") {
                    identifier = "budi.santoso@asiaraya.co.id"
                    password = "Password123!"
                }
                DemoAccountRow("Siti Rahmawati (HR Team)", "hr@asiaraya.co.id") {
                    identifier = "hr@asiaraya.co.id"
                    password = "Password123!"
                }
                DemoAccountRow("Agus Prasetyo (Finance)", "finance@asiaraya.co.id") {
                    identifier = "finance@asiaraya.co.id"
                    password = "Password123!"
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Versi Aplikasi 1.0.0 · HRIS REST API v2.17.0",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = Muted2
        )
    }
}

@Composable
private fun DemoAccountRow(
    title: String,
    email: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Bg2)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Ink
            )
            Text(
                text = email,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = Muted
            )
        }
        Icon(
            imageVector = Icons.Default.TouchApp,
            contentDescription = null,
            tint = Teal700,
            modifier = Modifier.size(18.dp)
        )
    }
}
