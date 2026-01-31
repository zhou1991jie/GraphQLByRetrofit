package com.example.grapqldemo6.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.grapqldemo6.ui.theme.Dimens

@Composable
fun WelcomeScreen(onNavigateToHome: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2196F3)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "欢迎使用宝可梦图鉴",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                modifier = Modifier.padding(bottom = Dimens.spacingLarge)
            )
            Text(
                text = "探索宝可梦的奇妙世界",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
            Button(
                onClick = onNavigateToHome,
                modifier = Modifier.padding(top = Dimens.spacingExtraLarge)
            ) {
                Text("开始探索")
            }
        }
    }
}