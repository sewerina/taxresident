package com.github.sewerina.taxresident.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.github.sewerina.taxresident.R
import kotlinx.coroutines.delay

@Composable
private fun SplashScreen(randomDraw: Int, alfa: Float, userName: String) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop,
            painter = painterResource(id = randomDraw),
            contentDescription = "hello image"
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(alpha = alfa)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier.size(96.dp),
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "logo icon",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "Приветствуем,\n${userName}!",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}

@Preview
@Composable
fun PreviewSplashScreen() {
    SplashScreen(R.drawable.hello_3, 1f, "Аркадий")
}

@Composable
fun AnimatedSplashScreen(randomDraw: Int, userName: String, navController: NavHostController) {
    var startAnimation by remember { mutableStateOf(value = false) }

    val alfaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 3000)
    )

    LaunchedEffect(key1 = Unit) {
        startAnimation = true
        delay(4000)
        navController.popBackStack()
        navController.toHome()
    }
    SplashScreen(randomDraw, alfaAnim.value, userName)
}