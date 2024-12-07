package com.su.communityconnect.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.su.communityconnect.model.User
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDate
import java.time.format.DateTimeFormatter

@Composable
fun OrganizerDetails(
    user: User,
    eventPostedDate: LocalDateTime,
    onPhoneClick: (String) -> Unit,
    onEmailClick: (String) -> Unit,
) {
    Row {
        // Profile Picture
        ProfilePicture(
            imageUrl = user.profilePictureUrl,
            displayName = user.displayName,
            onImageSelected = {}, // No image selection here
            size = 50,
            profileClicked = {}
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Organizer Name and Posted Date
        Column {
            Text(
                text = user.displayName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Posted on ${
                    eventPostedDate.date.toJavaLocalDate()
                        .format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                }",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondary
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Phone and Email Icons
        if (user.phone.isNotEmpty()) {
            IconButton(
                onClick = { onPhoneClick(user.phone) },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Phone,
                    contentDescription = "Call Organizer",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        IconButton(
            onClick = { onEmailClick(user.email) },
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                imageVector = Icons.Outlined.Email,
                contentDescription = "Email Organizer",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}