package com.su.communityconnect.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.su.communityconnect.R
import com.su.communityconnect.ui.components.PrimaryButton

@Composable
fun SplashScreen(onNavigateToSignIn: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.applogo),
                contentDescription = "App Logo",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(R.string.community).uppercase(),
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black)
            )
            Text(
                text = stringResource(R.string.connect).uppercase(),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Light)
            )
        }
        PrimaryButton(
            text = stringResource(R.string.lets_connect),
            onClick = onNavigateToSignIn,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalPadding = 40.dp
        )
    }
}
