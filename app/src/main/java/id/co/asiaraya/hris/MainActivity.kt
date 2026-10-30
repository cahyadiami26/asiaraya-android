package id.co.asiaraya.hris

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import id.co.asiaraya.hris.core.ui.theme.Bg
import id.co.asiaraya.hris.core.ui.theme.HrisTheme
import id.co.asiaraya.hris.feature.login.LoginScreen
import id.co.asiaraya.hris.feature.shell.ShellScreen
import id.co.asiaraya.hris.feature.splash.SplashScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as HrisApplication
        val repository = app.repository
        val sessionStore = app.sessionStore

        setContent {
            HrisTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Bg
                ) {
                    val isLoggedIn by repository.isLoggedInFlow.collectAsState(initial = false)
                    val serverUrl by repository.serverUrlFlow.collectAsState(initial = "https://hris.asiaraya.co.id")

                    var currentScreen by remember { mutableStateOf("splash") }

                    when (currentScreen) {
                        "splash" -> {
                            SplashScreen(
                                isLoggedIn = isLoggedIn,
                                onNavigateNext = { dest ->
                                    currentScreen = dest
                                }
                            )
                        }
                        "login" -> {
                            LoginScreen(
                                currentServerUrl = serverUrl,
                                onSaveServerUrl = { newUrl ->
                                    sessionStore.saveServerUrl(newUrl)
                                },
                                onLoginSubmit = { request ->
                                    repository.login(request).map { }
                                },
                                onLoginSuccess = {
                                    currentScreen = "shell"
                                }
                            )
                        }
                        "shell" -> {
                            ShellScreen(
                                repository = repository,
                                onLogoutDone = {
                                    currentScreen = "login"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
