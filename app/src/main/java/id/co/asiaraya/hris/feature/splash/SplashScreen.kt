package id.co.asiaraya.hris.feature.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.co.asiaraya.hris.R
import id.co.asiaraya.hris.core.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    isLoggedIn: Boolean,
    onNavigateNext: (destination: String) -> Unit
) {
    LaunchedEffect(isLoggedIn) {
        delay(1200) // Brief branded splash
        if (isLoggedIn) {
            onNavigateNext("shell")
        } else {
            onNavigateNext("login")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Navy900, Navy850, Navy950)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(2.dp, Teal400.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                    .background(Navy800),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hris_launcher_icon_1793357965887),
                    contentDescription = "Logo HRIS Asia Raya",
                    modifier = Modifier.size(76.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "HRIS Asia Raya",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = CardBg
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Enterprise HRIS Gateway",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Teal400,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp
                )
            )

            Spacer(modifier = Modifier.height(48.dp))

            CircularProgressIndicator(
                color = Teal400,
                strokeWidth = 2.5.dp,
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = "PT Asia Raya Group · v1.0.0",
            style = MaterialTheme.typography.bodySmall.copy(
                color = Muted2.copy(alpha = 0.7f),
                fontSize = 11.sp
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}
