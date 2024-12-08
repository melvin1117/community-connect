package com.su.communityconnect.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.su.communityconnect.R

@Composable
fun ImageUploaderField(
    modifier: Modifier = Modifier,
    label: String = stringResource(R.string.label_upload_images),
    maxImages: Int = 5,
    onImagesSelected: (List<Uri>) -> Unit,
    onSurface: Boolean = true,
    initialSelectedImages: List<Uri> = emptyList()
) {
    var selectedImages by remember { mutableStateOf<List<Uri>>(initialSelectedImages) }

    // File picker launcher
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        val newImages = (selectedImages + uris).take(maxImages)
        selectedImages = newImages
        onImagesSelected(newImages)
    }

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = if (onSurface) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Upload button
            OutlinedButton(
                onClick = { launcher.launch("image/*") },
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AttachFile,
                    contentDescription = stringResource(R.string.upload_icon_description),
                    tint = if (onSurface) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.upload_button),
                    color = if (onSurface) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onBackground
                )
            }

            // Thumbnails for selected images
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy((-16).dp), // Overlapping effect
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    selectedImages.take(3).forEach { uri ->
                        Image(
                            painter = rememberAsyncImagePainter(model = uri),
                            contentDescription = stringResource(R.string.uploaded_image_description),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                    }
                }

                // Display count if there are more images
                if (selectedImages.size > 3) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+${selectedImages.size - 3}",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center)
                        )
                    }
                }
            }
        }
    }
}
