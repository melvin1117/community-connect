package com.su.communityconnect.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun ProfilePicture(
    imageUrl: String?,
    displayName: String?,
    onImageSelected: (Uri) -> Unit,
    modifier: Modifier = Modifier,
    size: Int = 100
) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            onImageSelected(uri)
        }
    }

    val randomEmoji = listOf("🙂", "😄", "🌟", "💡", "🎉").random()
    val initials = displayName?.takeIf { it.isNotBlank() }
        ?.split(" ")
        ?.mapNotNull { it.firstOrNull()?.uppercaseChar() }
        ?.take(2)
        ?.joinToString("")
        ?: randomEmoji

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            .clickable { launcher.launch("image/*") }
            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
    ) {
        if (!imageUrl.isNullOrEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size((size - 4).dp)
                    .clip(CircleShape)
            )
        } else {
            Text(
                text = initials,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

